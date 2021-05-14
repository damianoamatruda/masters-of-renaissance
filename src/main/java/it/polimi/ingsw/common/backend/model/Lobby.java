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
    private int newGamePlayersCount;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
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

        // nickname validated, join lobby
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
            // send ack back + notify of needing to choose match's number of players
            notify(view, new ResJoin(isFirst));

            waiting.add(view);

            if (newGamePlayersCount != 0)
                waiting.forEach(v -> notify(v, new UpdateFreeSeats(newGamePlayersCount, newGamePlayersCount - waiting.size())));

            if (waiting.size() == newGamePlayersCount)
                startNewGame();
        }
    }

    public void prepareNewGame(View view, int newGamePlayersCount) {
        checkNickname(view);
        if (waiting.get(0) != view) {
            notify(view, new ErrAction("Command unavailable. You are not the first player who joined."));
            return;
        }
        if (newGamePlayersCount == 0) {
            notify(view, new ErrAction("Cannot have zero players in a game, please choose a valid amount."));
            return;
        }

        System.out.printf("Setting players count to %d.%n", newGamePlayersCount);
        this.newGamePlayersCount = newGamePlayersCount;

        // if > 0 there's still empty seats
        // else there's more waiting players than the amount needed to start this new game
        // can leverage for more info if wanted
        // (es. "count of players waiting in lobby for a new game to be started" -> with implications
        // (what happens if the first player doesn't start a new game? The specification doesn't say, ask teachers))
        if (newGamePlayersCount - waiting.size() > 0)
            waiting.subList(0, newGamePlayersCount).forEach(v -> notify(v, new UpdateFreeSeats(newGamePlayersCount, newGamePlayersCount - waiting.size())));

        if (waiting.size() >= this.newGamePlayersCount)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = newGamePlayersCount == 1 ?
                gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                gameFactory.getMultiGame(waiting.subList(0, newGamePlayersCount).stream().map(nicknames::get).toList());

        GameContext newContext = new GameContext(newGame, gameFactory);
        waiting.subList(0, newGamePlayersCount).forEach(v -> {
            newContext.register(v, nicknames.get(v));
            joined.put(v, newContext);
        });

        // remove players who joined from waiting list
        waiting.subList(0, newGamePlayersCount).clear();
        newGamePlayersCount = 0;
        // if there's still players waiting, send new request for newGamePlayersCount to start a new game
        if (waiting.size() > 0) {
            notify(waiting.get(0), new ResJoin(true)); // might want to separate ResJoin from player count choice
        }
    }

    public void exit(View view) {
        String nickname = nicknames.get(view);
        if (nickname != null) {
            GameContext context = joined.get(view);
            if (context != null) {
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
