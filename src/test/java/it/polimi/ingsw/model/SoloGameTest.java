package it.polimi.ingsw.model;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.resourcetypes.Coin;
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
    SoloGame game;
    Player player;

    /**
     * The setup: instantiation of game and the single player.
     */
    @BeforeEach
    void setup(){
        game = new SoloGame("Alessandro", new ArrayList<>(){{
            add(new DepotLeader(2, Coin.getInstance(),null,0));
            add(new DepotLeader(2, Coin.getInstance(),null,0));
        }}, 2, new ArrayList<>(),
                3, 4, new HashMap<>(), 0,24,
                3,3,7, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), null
        );

        player = game.getPlayers().get(0);

    }
    /*
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Test
    void blackPointsGetterTest(){
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        SoloGame solo = new SoloGame("player", new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0,0,0,0,0, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), stack);
        solo.incrementBlackPoints();
        solo.incrementBlackPoints();

        assertEquals(2,solo.getBlackPoints());
    }

    /**
     * Faith track related tests.
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
            game = new SoloGame("Alessandro", new ArrayList<>(), 0, new ArrayList<>(),
                    3, 4, new HashMap<>(), 0,24,
                    3,3,7, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), null
            );

            player = game.getPlayers().get(0);

            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet())-1; i++)
                player.incrementFaithPoints(game);
            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet()); i++)
                game.incrementBlackPoints();
        }

        /**
         * Ensures the player has earned the first Pope's favor.
         */
        @Test
        void FirstSoloReport(){
            assumeTrue(Collections.min(game.getVaticanSections().keySet()) - game.getVaticanSections().get(Collections.min(game.getVaticanSections().keySet()))[0] >= 1);

            int result = game.getVaticanSections().get(game.getBlackPoints())[1];

            assertEquals(result, player.getVictoryPoints());
        }

        /**
         * Ensures that if Lorenzo arrives first the player has lost.
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
     * Test for onTurnEnd method.
     */
    @Test
    void onTurnEnd(){
        Game game = new SoloGame("Alessandro", new ArrayList<>(), 0, new ArrayList<>(),
                3, 4, new HashMap<>(), 0,24,
                3,3,7, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), new ArrayList<>(){{
                    for(int i = 0; i < 4; i++)
                        add(new ActionTokenBlackMoveTwo());
        }}
        );

        Player player = game.getPlayers().get(0);

        assertEquals(player, game.onTurnEnd());

    }

    /**
     * Test for discardDevCards method.
     */
    @Test
    void discardDevCards(){
        try {
            player.discardLeader(game, 0);
            player.discardLeader(game, 0);
            assertEquals(2,player.getFaithPoints());
        }
        catch (Exception e){
            fail("Exception has been thrown");
        }
    }
}



