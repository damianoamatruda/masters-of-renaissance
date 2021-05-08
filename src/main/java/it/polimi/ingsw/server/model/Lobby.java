package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.*;

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
    private final List<View> waiting;
    private int countToNewGame;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        // this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
    }

    public void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            notify(view, new ErrNickname("You already have nickname \"" + nicknames.get(view) + "\"."));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            notify(view, new ErrNickname("Given nickname is empty."));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            notify(view, new ErrNickname("Nickname \"" + nickname + "\" already in use. Choose another one."));
            return;
        }
        nicknames.put(view, nickname);
        System.out.println("Set nickname \"" + nickname + "\".");

        waiting.add(view);
        if (waiting.size() == countToNewGame)
            startNewGame();

        notify(view, new ResJoin(waiting.size() == 1));
    }

    public void setCountToNewGame(View view, int count) {
        checkNickname(view);
        if (!isPlayerFirst(view)) {
            notify(view, new ErrAction("Command unavailable. You are not the first player who joined."));
            return;
        }
        System.out.println(String.format("Setting players count to %d.", count));
        this.countToNewGame = count;
        if (waiting.size() == countToNewGame)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = countToNewGame == 1 ?
            gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
            gameFactory.getMultiGame(waiting.subList(0, countToNewGame).stream().map(nicknames::get).toList());

        GameContext newContext = new GameContext(newGame, gameFactory);
        // games.add(newContext);
        waiting.subList(0, countToNewGame).forEach(v -> {
            newGame.addObserver(v);
            joined.put(v, newContext);
            v.update(new ResGameStarted()); // v.update(new ResGameStarted(newContext.getLeaderCards(), newContext.getDevelopmentCards(), newContext.getResContainers(), newContext.getProductions(), newContext.getMarket(), newContext.getDevCardGrid()));
        });
        waiting.subList(0, countToNewGame).clear();
        countToNewGame = 0;
    }

    public void exit(View view) {
        // TODO: Manage the exit of a view
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
