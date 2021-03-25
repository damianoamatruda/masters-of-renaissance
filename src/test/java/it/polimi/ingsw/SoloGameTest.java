package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test of SoloGame operations */
public class SoloGameTest {

    /** Tests if black has been incremented properly and if getter of blackPoints returns the correct value */
    @Test
    public void blackPointsGetterTest(){
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        SoloGame solo = new SoloGame(new Game(null,null,null), stack);
        solo.incrementBlackPoints();
        solo.incrementBlackPoints();

        assertEquals(2,solo.getBlackPoints());
    }
}
