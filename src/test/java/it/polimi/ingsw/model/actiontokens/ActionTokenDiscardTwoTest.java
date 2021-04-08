package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.devcardcolors.Blue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Test of the functionalities of the class "ActionTokenDiscardTwo".
 */
class ActionTokenDiscardTwoTest {
    // TODO: Add Javadoc
    @Test
    void trigger() {
        ActionToken token = new ActionTokenDiscardTwo(new Blue());
        List<List<Stack<DevelopmentCard>>> devCards = new ArrayList<>();
    }
}
