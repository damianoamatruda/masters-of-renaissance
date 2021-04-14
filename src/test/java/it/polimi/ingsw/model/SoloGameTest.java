package it.polimi.ingsw.model;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Test of SoloGame operations */
public class SoloGameTest {
    GameFactory factory = new FileGameFactory("src/main/resources/config.xml");
    SoloGame game;
    Player player;
    FaithTrack track;

    /**
     * The setup: instantiation of game and the single player.
     */
    @BeforeEach
    void setup() {
        player = new Player("Alessandro", true, new ArrayList<>(){{
            add(new DepotLeader(0, 2, null, factory.getResType("Coin"),null,0));
            add(new DepotLeader(0, 2, null, factory.getResType("Coin"),null,0));
        }}, new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 3, 0, 0);

        track = new FaithTrack(Set.of(
                new FaithTrack.VaticanSection(5, 8, 2),
                new FaithTrack.VaticanSection(12, 16, 3),
                new FaithTrack.VaticanSection(19, 24, 4)
        ), Set.of(
                new FaithTrack.YellowTile(15, 9),
                new FaithTrack.YellowTile(18, 12),
                new FaithTrack.YellowTile(24, 20)
        ));

        game = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, track, null, 24, 7);

    }
    /**
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Test
    void blackPointsGetterTest() {
        Player player = new Player("", true, new ArrayList<>(), new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        List<ActionToken> stack = new ArrayList<>();
//        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(factory.generateVaticanSections(), factory.generateYellowTiles()), stack, 24, 7);
        game.incrementBlackPoints();
        game.incrementBlackPoints();

        assertEquals(2,game.getBlackPoints());
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
            GameFactory f = new FileGameFactory("src/main/resources/config.xml");
            game = f.buildSoloGame("Alessandro");
            player = game.getPlayers().get(0);

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
    void onTurnEnd() throws AllInactiveException {
        GameFactory f = new FileGameFactory("src/main/resources/config.xml");
        game = f.buildSoloGame("Alessandro");
        player = game.getPlayers().get(0);

        assertEquals(player, game.onTurnEnd());

    }

    /**
     * Test the AlreadyActiveException when a Leader card is already active.
     */
    @Test
    void discardActiveDevCard() throws IllegalActivationException {

        player.getLeaders().get(0).activate(player);
        assertThrows(AlreadyActiveException.class, () -> player.discardLeader(game, 0));

    }
}



