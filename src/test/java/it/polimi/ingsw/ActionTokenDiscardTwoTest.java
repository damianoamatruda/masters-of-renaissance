package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ActionTokenDiscardTwoTest {

    @Test
    void trigger() {
        ActionToken token = new ActionTokenDiscardTwo(new Blue());
        List<List<Stack>> devCards = new ArrayList<>();
    }
}
