package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.DepotLeader;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
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
                new DepotLeader(2, coin, null, 0, 0),
                new DepotLeader(2, coin, null, 0, 0)
        ), new Warehouse(3), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 3, new PlayerSetup(0, 0, 0, Set.of()));

        track = new FaithTrack(Set.of(
                new FaithTrack.VaticanSection(0, 5, 8, 2),
                new FaithTrack.VaticanSection(0, 12, 16, 3),
                new FaithTrack.VaticanSection(0, 19, 24, 4)
        ), Set.of(
                new FaithTrack.YellowTile(15, 9),
                new FaithTrack.YellowTile(18, 12),
                new FaithTrack.YellowTile(24, 20)
        ), 24);

        ResourceType r1 = new ResourceType("r1", true);

        game = new SoloGame(player, null, null, List.of(), List.of(), List.of(), List.of(), List.of(
                new ActionToken() {
                    @Override
                    public void trigger(SoloGame game) {
                    }

                    @Override
                    public int getId() {
                        return 0;
                    }

                    @Override
                    public ReducedActionToken reduce() {
                        return null;
                    }
                }
        ), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), track, 7, 3);

    }

    /**
     * Tests if black has been incremented properly and if getter of blackPoints returns the correct value.
     */
    @Test
    void blackPointsGetterTest() {
        game.incrementBlackPoints(2);
        assertEquals(2, game.getBlackPoints());
    }

    /**
     * Test for onTurnEnd method.
     */
    @Test
    void onTurnEnd() {
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
        assertThrows(ActiveLeaderDiscardException.class, () -> player.discardLeader(new View(), game, player.getLeaders().get(0)));
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
            player.incrementFaithPoints(game, 7);
            game.incrementBlackPoints(8);
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
            game.incrementBlackPoints(16);
            game.onTurnEnd();
            assertAll(() -> assertEquals(2, player.getVictoryPoints()),
                    () -> assertFalse(player.isWinner()),
                    () -> assertTrue(game.isBlackWinner()));
        }
    }
}



