package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

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
