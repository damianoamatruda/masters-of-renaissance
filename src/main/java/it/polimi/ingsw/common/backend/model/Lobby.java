package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ErrAction;
import it.polimi.ingsw.common.events.mvevents.ResGoodbye;
import it.polimi.ingsw.common.events.mvevents.ResJoin;
import it.polimi.ingsw.common.events.mvevents.UpdateFreeSeats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Lobby extends ModelObservable {
    private final GameFactory gameFactory;
    // private final List<GameContext> games;
    private final Map<View, String> nicknames;
    private final Map<View, GameContext> joined;
    private final Map<String, GameContext> disconnected;
    private final List<View> waiting;
    private int playersCount;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        // this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.disconnected = new HashMap<>();
    }

    public void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            notify(view, new ErrAction("You already have nickname \"" + nicknames.get(view) + "\"."));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            notify(view, new ErrAction("Given nickname is empty."));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            notify(view, new ErrAction("Nickname \"" + nickname + "\" already in use. Choose another one."));
            return;
        }
        nicknames.put(view, nickname);
        if (disconnected.containsKey(nickname)) {
            System.out.println("Player \"" + nickname + "\" rejoined");
            notify(view, new ResJoin(false));
            GameContext toResume = disconnected.get(nickname);
            try {
                toResume.setActive(nickname, true);
            } catch (NoActivePlayersException e) {
                notify(view, new ErrAction(e.getMessage())); // TODO does this make sense?
            }
            toResume.resume(view, nicknames.get(view));
            joined.put(view, toResume);
            disconnected.remove(nickname);
        }
        else {
            System.out.println("Set nickname \"" + nickname + "\".");

            boolean isFirst = waiting.size() == 0;

            waiting.add(view);

            notify(view, new ResJoin(isFirst));

            // Sort of notifyBroadcast
            waiting.forEach(v -> notify(v, new UpdateFreeSeats(playersCount, playersCount - waiting.size())));

            if (waiting.size() == playersCount)
                startNewGame();
        }
    }

    public void prepareNewGame(View view, int playersCount) {
        checkNickname(view);
        if (!isPlayerFirst(view)) {
            notify(view, new ErrAction("Command unavailable. You are not the first player who joined."));
            return;
        }
        System.out.printf("Setting players count to %d.%n", playersCount);

        this.playersCount = playersCount;

        notify(view, new UpdateFreeSeats(this.playersCount, this.playersCount - waiting.size()));

        if (waiting.size() == this.playersCount)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = playersCount == 1 ?
                gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                gameFactory.getMultiGame(waiting.subList(0, playersCount).stream().map(nicknames::get).toList());

        GameContext newContext = new GameContext(newGame, gameFactory, new ArrayList<>(waiting.subList(0, playersCount)));
        // games.add(newContext);
        waiting.subList(0, playersCount).forEach(v -> {
            newContext.register(v, nicknames.get(v));
            joined.put(v, newContext);
        });
        waiting.subList(0, playersCount).clear();
        playersCount = 0;
    }

    public void exit(View view) {
        // TODO: Manage the exit of a view
        String nickname = nicknames.get(view);
        if(nickname != null) {
            GameContext context = joined.get(view);
            if(context != null) {
                context.removeObserver(view);
                try {
                    context.setActive(nickname, false);
                    disconnected.put(nickname, context);
                } catch (NoActivePlayersException e) {
                    disconnected.entrySet().removeIf(entry -> entry.getValue() == context);
                }
                joined.remove(view);
            }
            nicknames.remove(view);
        }
        notify(view, new ResGoodbye());
    }

    public boolean isPlayerFirst(View view) {
        return waiting.indexOf(view) == 0;
    }

    public void checkJoinedThen(View view, BiConsumer<GameContext, String> then) {
        if (checkJoined(view))
            then.accept(joined.get(view), nicknames.get(view));
    }

    private boolean checkNickname(View view) {
        if (!nicknames.containsKey(view)) {
            notify(view, new ErrAction("You must first request a nickname."));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        if (!checkNickname(view))
            return false;
        if (!joined.containsKey(view)) {
            notify(view, new ErrAction("You must first join a game."));
            return false;
        }
        return true;
    }
}
