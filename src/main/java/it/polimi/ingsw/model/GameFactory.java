package it.polimi.ingsw.model;

import java.util.List;

public interface GameFactory {
    Game buildMultiGame(List<String> nicknames);
    SoloGame buildSoloGame(String nickname);
}
