package it.polimi.ingsw.actiontokens;

import it.polimi.ingsw.DevelopmentCard;
import it.polimi.ingsw.actiontokens.ActionToken;
import it.polimi.ingsw.actiontokens.ActionTokenDiscardTwo;
import it.polimi.ingsw.devcardcolors.Blue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Test of the functionalities of the class "ActionTokenDiscardTwo"
 */
class ActionTokenDiscardTwoTest {

    @Test
    void trigger() {
        ActionToken token = new ActionTokenDiscardTwo(new Blue());
        List<List<Stack<DevelopmentCard>>> devCards = new ArrayList<>();
    }
}
