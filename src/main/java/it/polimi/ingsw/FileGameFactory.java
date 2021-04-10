package it.polimi.ingsw;

import it.polimi.ingsw.model.*;

import java.util.*;

public class FileGameFactory implements GameFactory {
    public Game buildMultiGame(List<String> nicknames) {
        // TODO: Implement
        return new Game(new ArrayList<>(), new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
    }

    public SoloGame buildSoloGame(String nickname) {
        // TODO: Implement
        return new SoloGame(null, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), new ArrayList<>(), 0, 0);
    }
}
