package it.polimi.ingsw.actiontokens;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.SoloGame;
import it.polimi.ingsw.actiontokens.ActionToken;
import it.polimi.ingsw.actiontokens.ActionTokenBlackMoveTwo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo"
 */
class ActionTokenBlackMoveTwoTest {

    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        SoloGame game = new SoloGame(new Game(new ArrayList<>()), null);
        token.trigger(game);

        assertEquals(game.getBlackPoints(),2);
    }
}
