package it.polimi.ingsw;

import it.polimi.ingsw.model.DevCardGrid;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameFactory;
import it.polimi.ingsw.model.SoloGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileGameFactory implements GameFactory {
    public Game buildMultiGame(List<String> nicknames) {
        // TODO: Implement
        return new Game(new ArrayList<>(), new DevCardGrid(new ArrayList<>(), 0, 0), null, new HashMap<>(), new HashMap<>(), 0, 0);
    }

    public SoloGame buildSoloGame(String nickname) {
        // TODO: Implement
        return new SoloGame(null, new DevCardGrid(new ArrayList<>(), 0, 0), null, new HashMap<>(), new HashMap<>(), new ArrayList<>(), 0, 0);
    }
}
