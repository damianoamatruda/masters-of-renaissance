package it.polimi.ingsw.model;

import it.polimi.ingsw.JavaGameFactory;
import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Test of SoloGame operations */
public class SoloGameTest {
    JavaGameFactory factory = new JavaGameFactory();
    ResourceTypeFactory resTypeFactory;
    SoloGame game;
    Player player;
    FaithTrack track = new FaithTrack(factory.generateVaticanSections(), factory.generateYellowTiles());

    /**
     * The setup: instantiation of game and the single player.
     */
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
        player = new Player("Alessandro", true, new ArrayList<>(){{
            add(new DepotLeader(0, 2, null, resTypeFactory.get("Coin"),null,0));
            add(new DepotLeader(0, 2, null, resTypeFactory.get("Coin"),null,0));
        }}, new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 3);
        game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(new JavaGameFactory().generateVaticanSections(), new JavaGameFactory().generateYellowTiles()), null, 24, 7);

    }
    /**
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Test
    void blackPointsGetterTest() {
        Player player = new Player("", true, new ArrayList<>(), new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        List<ActionToken> stack = new ArrayList<>();
        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(new JavaGameFactory().generateVaticanSections(), new JavaGameFactory().generateYellowTiles()), stack, 24, 7);
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
        OptionalInt firstVaticanReportTile;
        OptionalInt lastVaticanReportTile;
        OptionalInt firstVaticanSectionTile;

        /**
         * The setup: instantiation and advances till first Vatican report:
         * Player: one tile before first vatican report
         * Lorenzo: first vatican report tile
         */
        @BeforeEach
        void setup() {
            player = new Player("Alessandro", true, new ArrayList<>(), new Warehouse(3), new Strongbox(),
                    new Production(Map.of(), 0, Map.of(), 0), 3);
            game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(new JavaGameFactory().generateVaticanSections(), new JavaGameFactory().generateYellowTiles()), null, 24, 7);

            firstVaticanReportTile = track.getVaticanSections()
                    .stream()
                    .mapToInt(FaithTrack.VaticanSection::getFaithPointsEnd)
                    .min();
            lastVaticanReportTile = track.getVaticanSections()
                    .stream()
                    .mapToInt(FaithTrack.VaticanSection::getFaithPointsEnd)
                    .max();
            firstVaticanSectionTile = track.getVaticanSections()
                    .stream()
                    .mapToInt(FaithTrack.VaticanSection::getFaithPointsBeginning)
                    .min();

            for(int i = 0; i < firstVaticanReportTile.getAsInt()-1; i++)
                player.incrementFaithPoints(game);
            for(int i = 0; i < firstVaticanReportTile.getAsInt(); i++)
                game.incrementBlackPoints();
        }

        /**
         * Ensures the player has earned the first Pope's favor.
         */
        @Test
        void FirstSoloReport() {
            assumeTrue(firstVaticanReportTile.getAsInt() - firstVaticanSectionTile.getAsInt() >= 1);

            int result = track.getVaticanSectionReport(game.getBlackPoints()).getVictoryPoints();

            assertEquals(result, player.getVictoryPoints());
        }

        /**
         * Ensures that if Lorenzo arrives first the player has lost.
         */
        @Test
        void losingGame() {

            assumeTrue(firstVaticanReportTile.getAsInt() - firstVaticanSectionTile.getAsInt() >= 1);

            for(int i = 0; i < lastVaticanReportTile.getAsInt() - firstVaticanReportTile.getAsInt(); i++)
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
    void onTurnEnd() {
        player = new Player("Alessandro", true, new ArrayList<>(), new Warehouse(3), new Strongbox(),
                new Production(Map.of(), 0, Map.of(), 0), 3);
        game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(new JavaGameFactory().generateVaticanSections(), new JavaGameFactory().generateYellowTiles()), new ArrayList<>() {{
            for(int i = 0; i < 4; i++)
                add(new ActionTokenBlackMoveTwo());
        }}, 24, 7);

        assertEquals(player, game.onTurnEnd());

    }

    /**
     * Test for discardDevCards method.
     */
    @Test
    void discardDevCards() {
        try {
            player.discardLeader(game, 0);
            player.discardLeader(game, 0);
            assertEquals(2,player.getFaithPoints());
        }
        catch (Exception e){
            e.printStackTrace();
            fail("Exception has been thrown");
        }
    }
}



