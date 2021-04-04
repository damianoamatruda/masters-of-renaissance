package it.polimi.ingsw;

import it.polimi.ingsw.actiontokens.ActionToken;
import it.polimi.ingsw.actiontokens.ActionTokenBlackMoveOneShuffle;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test of SoloGame operations */
public class SoloGameTest {
    /** Tests if black has been incremented properly and if getter of blackPoints returns the correct value */
    @Test
    void blackPointsGetterTest(){
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        SoloGame solo = new SoloGame(new ArrayList<>(), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, stack, OriginalGame.getVaticanSections(), OriginalGame.getYellowTiles());
        solo.incrementBlackPoints();
        solo.incrementBlackPoints();

        assertEquals(2,solo.getBlackPoints());
    }

    /**
     * Test for hasEnded method
     */
    @Test
    void hasEnded(){}

    /**
     * Test for onTurnEnd method
     */
    @Test
    void onTurnEnd(){}

    /**
     * Test for discardDevCards method
     */
    @Test
    void discardDevCards(){}
}



