package it.polimi.ingsw;

import it.polimi.ingsw.actiontokens.ActionToken;
import it.polimi.ingsw.actiontokens.ActionTokenBlackMoveOneShuffle;
import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
        SoloGame solo = new SoloGame(new ArrayList<>(), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0,0,0,0,0, stack, OriginalGame.generateVaticanSections(), OriginalGame.generateYellowTiles());
        solo.incrementBlackPoints();
        solo.incrementBlackPoints();

        assertEquals(2,solo.getBlackPoints());
    }

    /**
     * Faith track related tests
     */
    @Nested
    class FaithTrackTest{
        SoloGame game;
        Player player;

        /**
         * The setup: instantiation and advances till first Vatican report:
         * Player: one tile before first vatican report
         * Lorenzo: first vatican report tile
         */
        @BeforeEach
        void setup(){
            game = new SoloGame(List.of("Alessandro"), new ArrayList<>(), 0, new ArrayList<>(),
                    3, 4, new HashMap<>(), 0,24,
                    3,3,7, null,
                    OriginalGame.generateVaticanSections(), OriginalGame.generateYellowTiles());

            player = game.getPlayers().get(0);

            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet())-1; i++)
                player.incrementFaithPoints(game);
            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet()); i++)
                game.incrementBlackPoints();
        }

        /**
         * Ensures the player has earned the first Pope's favor
         */
        @Test
        void FirstSoloReport(){
            assumeTrue(Collections.min(game.getVaticanSections().keySet()) - game.getVaticanSections().get(Collections.min(game.getVaticanSections().keySet()))[0] >= 1);

            int result = game.getVaticanSections().get(game.getBlackPoints())[1];

            assertEquals(result, player.getVictoryPoints());
        }

        /**
         * Ensures that if Lorenzo arrives first the player has lost
         */
        @Test
        void losingGame(){
            assumeTrue(Collections.min(game.getVaticanSections().keySet()) - game.getVaticanSections().get(Collections.min(game.getVaticanSections().keySet()))[0] >= 1);

            for(int i = 0; i < Collections.max(game.getVaticanSections().keySet()) - Collections.min(game.getVaticanSections().keySet()); i++)
                game.incrementBlackPoints();
            game.hasEnded();
            assertAll(()->assertEquals(2, player.getVictoryPoints()),
                    ()->assertFalse(player.isWinner()),
                    ()->assertTrue(game.isBlackWinner()));
        }

    }

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



