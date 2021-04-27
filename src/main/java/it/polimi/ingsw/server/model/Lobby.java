package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    final private GameFactory gameFactory;
    final private List<GameContext> games;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.games = new ArrayList<>();
    }
}
