package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.*;

public class Lobby {
    private final GameFactory gameFactory;
    private final List<GameContext> games;
    private final Map<Player, GameContext> joined;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.games = new ArrayList<>();
        this.joined = new HashMap<>();
    }

    public void joinLobby(Player player) {
        joined.put(player, new GameContext(gameFactory.getSoloGame("")));
    }

    public Optional<GameContext> getJoinedGame(Player player) {
        return Optional.ofNullable(joined.get(player));
    }
}
