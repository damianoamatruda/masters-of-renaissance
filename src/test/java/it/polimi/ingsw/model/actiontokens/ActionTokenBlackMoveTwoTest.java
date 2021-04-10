package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.JavaGameFactory;
import it.polimi.ingsw.model.SoloGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo".
 */
class ActionTokenBlackMoveTwoTest {
    /**
     * Ensures Lorenzo's marker has advanced by 2 after triggering the "Advance by 2" token.
     */
    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        SoloGame game = new JavaGameFactory().buildSoloGame("");
        token.trigger(game);

        assertEquals(game.getBlackPoints(),2);
    }
}
