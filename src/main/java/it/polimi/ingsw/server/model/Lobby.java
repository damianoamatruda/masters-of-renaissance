package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.*;

public class Lobby {
    private final GameFactory gameFactory;
    private final List<GameContext> games;
    private final Map<String, GameContext> joined;
    private final List<String> waiting;
    private int countToNewGame;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
    }

    public void joinLobby(String nickname) {
        waiting.add(nickname);
        if(waiting.size() == countToNewGame) {
            startNewGame();
        }
    }

    public Optional<GameContext> getJoinedGame(String nickname) {
        return Optional.ofNullable(joined.get(nickname));
    }

    public void setCountToNewGame(int count) {
        this.countToNewGame = count;
        if (waiting.size() == countToNewGame) startNewGame();
    }

    public void startNewGame() {
        Game newGame = countToNewGame == 1 ? gameFactory.getSoloGame(waiting.get(0)) : gameFactory.getMultiGame(waiting.subList(0,countToNewGame));
        GameContext newContext = new GameContext(newGame);
        games.add(newContext);
        waiting.subList(0,countToNewGame).forEach(p -> joined.put(p, newContext));
        waiting.subList(0,countToNewGame).clear();
        countToNewGame = 0;
    }

    public boolean isPlayerFirst(String nickname) {
        return waiting.indexOf(nickname) == 0;
    }

    public Optional<Player> getPlayer(String nickname) {
        return joined.get(nickname).getPlayer(nickname);
    }
}
