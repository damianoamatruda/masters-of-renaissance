package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.ModelObserver;
import it.polimi.ingsw.common.events.*;

import java.util.*;

public class Lobby {
    private final GameFactory gameFactory;
    // private final List<GameContext> games;
    private final Map<ModelObserver, String> nicknames;
    private final Map<ModelObserver, GameContext> joined;
    private final List<ModelObserver> waiting;
    private int countToNewGame;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        // this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
    }

    public void joinLobby(ModelObserver view, String nickname) {
        if (nicknames.containsKey(view)) {
            view.update(new ErrNickname("You already have nickname \"" + nicknames.get(view) + "\"."));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            view.update(new ErrNickname("Given nickname is empty."));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            view.update(new ErrNickname("Nickname \"" + nickname + "\" already in use. Choose another one."));
            return;
        }
        nicknames.put(view, nickname);
        System.out.println("Set nickname \"" + nickname + "\".");

        waiting.add(view);
        if (waiting.size() == countToNewGame)
            startNewGame();

        view.update(new ResNickname(waiting.size() == 1));
    }

    public void setCountToNewGame(ModelObserver view, int count) {
        checkNickname(view);
        if (!isPlayerFirst(view)) {
            view.update(new ErrAction("Command unavailable. You are not the first player who joined."));
            return;
        }
        System.out.println("Set count of players.");
        this.countToNewGame = count;
        if (waiting.size() == countToNewGame)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = countToNewGame == 1 ? gameFactory.getSoloGame(nicknames.get(waiting.get(0))) : gameFactory.getMultiGame(waiting.subList(0, countToNewGame).stream().map(nicknames::get).toList());
        GameContext newContext = new GameContext(newGame, gameFactory);
        // games.add(newContext);
        waiting.subList(0, countToNewGame).forEach(v -> {
            joined.put(v, newContext);
            v.update(new ResGameStarted()); // v.update(new ResGameStarted(newContext.getLeaderCards(), newContext.getDevelopmentCards(), newContext.getResContainers(), newContext.getProductions(), newContext.getMarket(), newContext.getDevCardGrid()));
        });
        waiting.subList(0, countToNewGame).clear();
        countToNewGame = 0;
    }

    public void exit(ModelObserver view) {
        // TODO: Manage the exit of a view
        view.update(new ResGoodbye());
    }

    public boolean isPlayerFirst(ModelObserver view) {
        return waiting.indexOf(view) == 0;
    }

    public Optional<GameContext> getJoinedGame(ModelObserver view) {
        checkJoined(view);
        return Optional.ofNullable(joined.get(view));
    }

    public String getPlayer(ModelObserver view) {
        if (!checkJoined(view))
            return null;
//        return joined.get(view).getPlayer(nicknames.get(view)).orElseThrow();
        return nicknames.get(view);
    }

    public boolean checkNickname(ModelObserver view) {
        if (!nicknames.containsKey(view)) {
            view.update(new ErrAction("You must first request a nickname."));
            return false;
        }
        return true;
    }

    public boolean checkJoined(ModelObserver view) {
        if (!checkNickname(view))
            return false;
        if (!joined.containsKey(view)) {
            view.update(new ErrAction("You must first join a game."));
            return false;
        }
        return true;
    }
}
