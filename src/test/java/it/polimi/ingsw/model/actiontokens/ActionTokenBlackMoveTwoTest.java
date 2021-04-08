package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.SoloGame;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo".
 */
class ActionTokenBlackMoveTwoTest {
    // TODO: Add Javadoc
    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        SoloGame game = new SoloGame("player", new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0,24,3,3,7, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), null);
        token.trigger(game);

        assertEquals(game.getBlackPoints(),2);
    }
}
