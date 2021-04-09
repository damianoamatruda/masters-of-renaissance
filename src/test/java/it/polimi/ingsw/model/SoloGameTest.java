package it.polimi.ingsw.model;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.Coin;
import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import java.util.*;

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
    void setup() {
        player = new Player("Alessandro", true, List.of(
                new DepotLeader(2, Coin.getInstance(),null,0),
                new DepotLeader(2, Coin.getInstance(),null,0)
        ), new Warehouse(3), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 3);
        game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), null, 0, 0);

    }
    /*
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Disabled("To be fixed") // TODO
    @Test
    void blackPointsGetterTest() {
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        Player player = new Player("", true, new ArrayList<>(), new Warehouse(3), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), stack, 0, 0);
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
        void setup() {
            player = new Player("Alessandro", true, new ArrayList<>(), new Warehouse(3), new Strongbox(),
                    new Production<>(Map.of(), 0, Map.of(), 0), 3);
            game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), null, 0, 0);

            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet())-1; i++)
                player.incrementFaithPoints(game);
            for(int i = 0; i < Collections.min(game.getVaticanSections().keySet()); i++)
                game.incrementBlackPoints();
        }

        /**
         * Ensures the player has earned the first Pope's favor.
         */
        @Test
        void FirstSoloReport() {
            assumeTrue(Collections.min(game.getVaticanSections().keySet()) - game.getVaticanSections().get(Collections.min(game.getVaticanSections().keySet()))[0] >= 1);

            int result = game.getVaticanSections().get(game.getBlackPoints())[1];

            assertEquals(result, player.getVictoryPoints());
        }

        /**
         * Ensures that if Lorenzo arrives first the player has lost.
         */
        @Disabled("To fix") // TODO
        @Test
        void losingGame() {
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
    @Disabled("To be fixed") // TODO
    @Test
    void onTurnEnd() {
        player = new Player("Alessandro", true, new ArrayList<>(), new Warehouse(3), new Strongbox(),
                new Production<>(Map.of(), 0, Map.of(), 0), 3);
        game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), new ArrayList<>() {{
            for(int i = 0; i < 4; i++)
                add(new ActionTokenBlackMoveTwo());
        }}, 0, 0);

        assertEquals(player, game.onTurnEnd());

    }

    /**
     * Test for discardDevCards method.
     */
    @Disabled("To be fixed") // TODO
    @Test
    void discardDevCards() {
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



