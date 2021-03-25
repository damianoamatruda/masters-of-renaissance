package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveOneShuffle"
 */
class ActionTokenBlackMoveOneShuffleTest {

    @Test
    @DisplayName("Move one and shuffle")
    void testBlackAdvancement() {
        ActionToken token = new ActionTokenBlackMoveOneShuffle();
        List<ActionToken> stack = new ArrayList<>();
        stack.add(token);
        SoloGame solo = new SoloGame(new Game(null,null,null), stack);


        token.trigger(solo);

        assertEquals(1, solo.getBlackPoints());
    }

    @Test
    void testShuffle(){
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        SoloGame solo = new SoloGame(new Game(null,null,null), stack);
        List stack2 = new ArrayList(stack);
        stack2.remove(0);

        stack.get(0).trigger(solo);
        assertNotEquals(stack, stack2);
    }
}