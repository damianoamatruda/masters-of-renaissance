package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.*;

public class Lobby {
    private final GameFactory gameFactory;
    private final List<GameContext> games;
    private final Map<String, GameContext> joined;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.games = new ArrayList<>();
        this.joined = new HashMap<>();
    }

    public void joinLobby(String nickname) {
        joined.put(nickname, new GameContext(gameFactory.getSoloGame(nickname)));
    }

    public Optional<GameContext> getJoinedGame(String nickname) {
        return Optional.ofNullable(joined.get(nickname));
    }
}
