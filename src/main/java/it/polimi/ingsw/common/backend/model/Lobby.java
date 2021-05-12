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
    private final List<String> disconnected;
    private final List<View> waiting;
    private int countToNewGame;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        // this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.disconnected = new ArrayList<>();
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
        if (disconnected.contains(nickname)) {
            System.out.println("Player \"" + nickname + "\" rejoined");
            notify(view, new ResJoin(false));

            disconnected.remove(nickname);
            try {
                joined.get(view).setActive(nickname, true);
            } catch (NoActivePlayersException e) {
                notify(view, new ErrAction(e.getMessage())); // TODO does this make sense?
            }
            joined.get(view).getGame().resume(view, nicknames.get(view));
        }
        else {
            System.out.println("Set nickname \"" + nickname + "\".");

            waiting.add(view);

            notify(view, new ResJoin(waiting.size() == 1));

            if (countToNewGame != 0)
                notify(view, new UpdateFreeSeats(countToNewGame, countToNewGame - waiting.size()));

            if (waiting.size() == countToNewGame)
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

        this.countToNewGame = playersCount;

        notify(view, new UpdateFreeSeats(countToNewGame, countToNewGame - waiting.size()));
        
        if (waiting.size() == countToNewGame)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = countToNewGame == 1 ?
            gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
            gameFactory.getMultiGame(waiting.subList(0, countToNewGame).stream().map(nicknames::get).toList());

        GameContext newContext = new GameContext(newGame, gameFactory, new ArrayList<>(waiting.subList(0, countToNewGame)));
        // games.add(newContext);
        waiting.subList(0, countToNewGame).forEach(v -> {
            newGame.addObserver(v, nicknames.get(v));
            joined.put(v, newContext);
        });
        waiting.subList(0, countToNewGame).clear();
        countToNewGame = 0;
    }

    public void exit(View view) {
        // TODO: Manage the exit of a view
        String nickname = nicknames.get(view);
        joined.get(view).getGame().removeObserver(view);
        try {
            joined.get(view).setActive(nickname, false);
        } catch (NoActivePlayersException e) {
            joined.remove(view);
        }
        // disconnected.add(nickname);
        nicknames.remove(view);
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
