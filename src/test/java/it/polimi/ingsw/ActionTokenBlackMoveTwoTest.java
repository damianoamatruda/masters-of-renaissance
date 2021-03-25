package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo"
 */
class ActionTokenBlackMoveTwoTest {

    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        SoloGame game = new SoloGame(new Game(null,null,null), null);
        token.trigger(game);

        assertEquals(game.getBlackPoints(),2);
    }
}
