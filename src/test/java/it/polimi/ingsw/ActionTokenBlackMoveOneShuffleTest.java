package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class ActionTokenBlackMoveOneShuffleTest {

    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveOneShuffle();
        Stack<ActionToken> stack = new Stack<>();
        stack.push(token);
        SoloGame solo = new SoloGame(new Game(), stack);


        token.trigger(solo);
    }
}