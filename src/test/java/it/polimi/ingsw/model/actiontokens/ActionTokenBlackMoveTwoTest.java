package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.SoloGame;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo".
 */
class ActionTokenBlackMoveTwoTest {
    // TODO: Add Javadoc
    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        SoloGame game = new GameFactory().buildSoloGame("");
        token.trigger(game);

        assertEquals(game.getBlackPoints(),2);
    }
}
