package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    final private GameFactory gameFactory;
    final private List<GameContext> games;
    final private Map<Socket, GameContext> joined;
    final private Map<Socket, Player> players;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.players = new HashMap<>();
    }

    public void joinLobby(Socket client) {
        joined.put(client, new GameContext(gameFactory.getSoloGame("")));
    }

    public GameContext getJoinedGame(Socket c) {
        return joined.get(c);
    }

    public void assignPlayer(Socket c, Player p) {
        players.put(c, p);
    }

    public Player getPlayer(Socket c) {
        return players.get(c);
    }


}
