package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.DepotLeader;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
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
        ), new Warehouse(3), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 3, 0, 0, 0, Set.of());

        track = new FaithTrack(Set.of(
                new FaithTrack.VaticanSection(5, 8, 2),
                new FaithTrack.VaticanSection(12, 16, 3),
                new FaithTrack.VaticanSection(19, 24, 4)
        ), Set.of(
                new FaithTrack.YellowTile(15, 9),
                new FaithTrack.YellowTile(18, 12),
                new FaithTrack.YellowTile(24, 20)
        ));

        game = new SoloGame(player, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), null, track, List.of(
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
    void onTurnEnd() throws NoActivePlayersException {
        player = game.getPlayers().get(0);
        game.onTurnEnd();
        assertEquals(player, game.getCurrentPlayer());
    }

    /**
     * Test the AlreadyActiveException when a Leader card is already active.
     */
    @Test
    void discardActiveDevCard() throws IllegalArgumentException, CardRequirementsNotMetException {
        player.getLeaders().get(0).activate(player);
        assertThrows(ActiveLeaderDiscardException.class, () -> player.discardLeader(game, player.getLeaders().get(0)));
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
            game.end();
            assertAll(() -> assertEquals(2, player.getVictoryPoints()),
                    () -> assertFalse(player.isWinner()),
                    () -> assertTrue(game.isBlackWinner()));
        }
    }
}



