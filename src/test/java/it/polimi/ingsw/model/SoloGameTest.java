package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Test of SoloGame operations */
public class SoloGameTest {
    SoloGame game;
    Player player;
    FaithTrack track;

    final ResourceType coin = new ResourceType("Coin", true);

    /**
     * The setup: instantiation of game and the single player.
     */
    @BeforeEach
    void setup() {
        player = new Player("Alessandro", true, List.of(
                new DepotLeader(2, coin, null, 0),
                new DepotLeader(2, coin, null, 0)
        ), new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 3, 0, 0);

        track = new FaithTrack(Set.of(
                new FaithTrack.VaticanSection(5, 8, 2),
                new FaithTrack.VaticanSection(12, 16, 3),
                new FaithTrack.VaticanSection(19, 24, 4)
        ), Set.of(
                new FaithTrack.YellowTile(15, 9),
                new FaithTrack.YellowTile(18, 12),
                new FaithTrack.YellowTile(24, 20)
        ));

        game = new SoloGame(player, new DevCardGrid(List.of(), 0, 0), null, track, List.of(
                new ActionTokenBlackMoveTwo()
        ), 24, 7);

    }

    /**
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Test
    void blackPointsGetterTest() {
        game.incrementBlackPoints();
        game.incrementBlackPoints();
        assertEquals(2, game.getBlackPoints());
    }

    /**
     * Test for onTurnEnd method.
     */
    @Test
    void onTurnEnd() throws AllInactiveException {
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

    /**
     * Faith track related tests.
     */
    @Nested
    class FaithTrackTest {

        /**
         * The setup: instantiation and advances till first Vatican report: Player: one tile before first vatican report
         * Lorenzo: first vatican report tile
         */
        @BeforeEach
        void setup() {
            for (int i = 0; i < 7; i++)
                player.incrementFaithPoints(game);
            for (int i = 0; i < 8; i++)
                game.incrementBlackPoints();
        }

        /**
         * Ensures the player has earned the first Pope's favor.
         */
        @Test
        void FirstSoloReport() {
            int result = track.getVaticanSectionReport(game.getBlackPoints()).getVictoryPoints();
            assertEquals(result, player.getVictoryPoints());
        }

        /**
         * Ensures that if Lorenzo arrives first the player has lost.
         */
        @Test
        void losingGame() {
            for (int i = 0; i < 16; i++)
                game.incrementBlackPoints();
            game.hasEnded();
            assertAll(() -> assertEquals(2, player.getVictoryPoints()),
                    () -> assertFalse(player.isWinner()),
                    () -> assertTrue(game.isBlackWinner()));
        }
    }
}



