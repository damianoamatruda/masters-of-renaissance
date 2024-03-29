package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of base game operations.
 */

public class GameTest {
    Game game;
    List<Player> initialOrder;


    /**
     * First instantiation of Game with 3 players.
     */
    @BeforeEach
    void setup() {
        List<String> shuffledNicknames = new ArrayList<>(List.of("Alessandro", "Damiano", "Marco"));
        Collections.shuffle(shuffledNicknames);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0, List.of(),
                    new Warehouse(3), new Strongbox(),
                    new ResourceTransactionRecipe(Map.of(), 2, Map.of(), 1),
                    3,
                    new PlayerSetup(0, 0, 0, Set.of()));
            players.add(player);
        }

        ResourceType r1 = new ResourceType("r1", "", true);
        Market market = new Market(
                Map.of(r1, 13), 4, r1);

        game = new Game(
                players,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new DevCardGrid(List.of(), 3, 4), market,
                new FaithTrack(Set.of(
                        new FaithTrack.VaticanSection(0, 5, 8, 2),
                        new FaithTrack.VaticanSection(0, 12, 16, 3),
                        new FaithTrack.VaticanSection(0, 19, 24, 4)
                ), Set.of(
                        new FaithTrack.YellowTile(15, 9),
                        new FaithTrack.YellowTile(18, 12),
                        new FaithTrack.YellowTile(24, 20)
                ), 24),
                7, 3);

        initialOrder = game.getPlayers();
    }

    /**
     * Checks some basic instantiation effects, more specifically if player 1 has inkwell and if initial turn number is
     * 1.
     */
    @Test
    @DisplayName("Game should already be instantiated")
    void instantiationTest() {
        assertAll(() -> assertTrue(game.getPlayers().get(0).hasInkwell()));
    }

    /**
     * Checks that first advance does not give any points.
     */
    @Test
    void firstAdvanceNoPts() {
        incrementAndCheckSection(game.getPlayers().get(0), 1);
        assertEquals(0, game.getPlayers().get(0).getVictoryPoints());
    }

    /**
     * Tests that any player that has not reached any yellow tiles (nor sections, nor has cards / resources) has no
     * yellow tile bonus points.
     */
    @Test
    void noYellowTilesEndOfGame() {
        game.getPlayers().stream().filter(p -> !p.equals(game.getPlayers().get(0))).forEach(p -> p.setActive(false));
        incrementAndCheckSection(game.getPlayers().get(0), 24);
        game.onTurnEnd();
        assertAll(() -> assertTrue(game.isEnded()),
                () -> assertEquals(0, game.getPlayers().get(1).getVictoryPoints()));
    }

    private void incrementAndCheckSection(Player player, int points) {
        player.incrementFaithPoints(game, points);
        game.activateVaticanSections();
    }

    /**
     * Nested class for onIncrementFaithPoints method - first Vatican Report related tests.
     */
    @Nested
    @DisplayName("First Vatican Report Tests")
    class FirstVaticanReport {
        /**
         * Simulates a possible scenario after triggering 1st Vatican Report. Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 5;</li>
         *     <li>Player 2: 8;</li>
         *     <li>Player 3: 0.</li>
         * </ol>
         */
        @BeforeEach
        void advancePlayers() {
            incrementAndCheckSection(game.getPlayers().get(0), 5);
            incrementAndCheckSection(game.getPlayers().get(1), 8);
        }

        /**
         * Ensures that Player 1 has received the 1st set of Vatican favor points.
         */
        @Test
        void firstVaticanReportPtsObtained() {
            assertEquals(2, game.getPlayers().get(0).getVictoryPoints());
        }

        /**
         * Ensures that Player 3 has not received the 1st set of Vatican favor points.
         */
        @Test
        void firstVaticanReportPtsNotObtained() {
            assertEquals(0, game.getPlayers().get(2).getVictoryPoints());
        }

        /**
         * Ensures that the Game has not ended yet.
         */
        @Test
        void hasTheGameEnded() {
            assertFalse(game.isEnded());
        }
    }

    /**
     * Nested class for onIncrementFaithPoints method - second Vatican Report related tests.
     */
    @Nested

    @DisplayName("Second Vatican Report Tests")
    class SecondVaticanReport {
        /**
         * Simulates a possible scenario after triggering 2nd Vatican Report. Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 5;</li>
         *     <li>Player 2: 16;</li>
         *     <li>Player 3: 8.</li>
         * </ol>
         */
        @BeforeEach
        void advancePlayers() {
            incrementAndCheckSection(game.getPlayers().get(0), 5);
            incrementAndCheckSection(game.getPlayers().get(2), 8);

            incrementAndCheckSection(game.getPlayers().get(1), 16);
        }

        /**
         * Checks the correct progressive quantity of points earned so far by Player 1 (yellow tiles points excluded).
         */
        @Test
        void secondVaticanReportPtsAlessandro() {
            assertEquals(2, game.getPlayers().get(0).getVictoryPoints());
        }

        /**
         * Checks the correct progressive quantity of points earned so far by Player 2 (yellow tiles points excluded).
         */
        @Test
        void secondVaticanReportPtsDamiano() {
            assertEquals(12, game.getPlayers().get(1).getVictoryPoints());
        }

        /**
         * Checks the correct progressive quantity of points earned so far by Player 3 (yellow tiles points excluded).
         */
        @Test
        void secondVaticanReportPtsMarco() {
            assertEquals(2, game.getPlayers().get(2).getVictoryPoints());
        }

    }

    /**
     * Nested class for last Vatican Report and late game related tests.
     */
    @Nested

    @DisplayName("Last Vatican Report and Late Game Tests")
    class LastVaticanReport {
        /**
         * Simulates a possible scenario after triggering 1st Vatican Report. Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 24;</li>
         *     <li>Player 2: 20;</li>
         *     <li>Player 3: 16.</li>
         * </ol>
         */
        @BeforeEach
        void advancePlayers() {
            incrementAndCheckSection(game.getPlayers().get(0), 5);
            incrementAndCheckSection(game.getPlayers().get(2), 8);

            incrementAndCheckSection(game.getPlayers().get(1), 16);

            incrementAndCheckSection(game.getPlayers().get(2), 8);
            incrementAndCheckSection(game.getPlayers().get(1), 4);
            incrementAndCheckSection(game.getPlayers().get(0), 19);
        }

        /**
         * Ensures that the previously executed methods in advancePlayers() have not had any side effects on the order
         * of the players.
         */
        @RepeatedTest(value = 10)
        void arePlayersStillInSameOrder() {
            assertAll(() -> assertEquals(game.getPlayers().get(0).getNickname(), initialOrder.get(0).getNickname()),
                    () -> assertEquals(game.getPlayers().get(1).getNickname(), initialOrder.get(1).getNickname()),
                    () -> assertEquals(game.getPlayers().get(2).getNickname(), initialOrder.get(2).getNickname()));
        }

        /**
         * Ensures that the previously executed methods in advancePlayers() have not had any side effects on the number
         * of resources stored.
         */
        @Test
        void doPlayersHaveStorableResources() {
            assertAll(() -> assertEquals(game.getPlayers().get(0).getResourcesCount(), 0),
                    () -> assertEquals(game.getPlayers().get(1).getResourcesCount(), 0),
                    () -> assertEquals(game.getPlayers().get(2).getResourcesCount(), 0));
        }

        /**
         * Nested class for onIncrementFaithPoints method - 3rd Vatican section tests before calling isLastRound().
         */
        @Nested
        @DisplayName("Last Vatican Report tests before deciding winner")
        class LateGameBeforeWinnerCalcs {
            /**
             * Checks the correct progressive quantity of points earned so far by Player 2 (yellow tiles points
             * excluded).
             */
            @Test
            void lastVaticanReportPtsObtained() {
                assertEquals(19, game.getPlayers().get(1).getVictoryPoints());
            }

            /**
             * Checks the correct progressive quantity of points earned so far by Player 1 (yellow tiles points
             * excluded).
             */
            @Test
            void lastVaticanReportPtsObtainedLead() {
                assertEquals(26, game.getPlayers().get(0).getVictoryPoints());
            }

            /**
             * Checks the correct progressive quantity of points earned so far by Player 3 (yellow tiles points
             * excluded).
             */
            @Test
            void lastVaticanReportPtsNotObtained() {
                assertEquals(11, game.getPlayers().get(2).getVictoryPoints());
            }
        }

        /**
         * Nested class for 3rd Vatican section tests after choosing winner.
         */
        @Nested
        @DisplayName("Last Vatican Report tests after deciding winner")
        class LateGameAfterWinnerCalcs {
            /**
             * Closes the Game (and does the last calcs to determine winner) before the following tests.
             */
            @BeforeEach
            void endGame() {
                game.onAddToDevSlot(7);
                game.getPlayers().get(1).setActive(false);
                game.getPlayers().get(2).setActive(false);
                game.onTurnEnd();
            }

            /**
             * Ensures the Game has actually ended.
             */
            @Test
            void hasTheGameEnded() {
                assertTrue(game.isEnded());
            }

            /**
             * Checks the total score achieved by Player 1.
             */
            @RepeatedTest(value = 10)
            void ptsAlessandroAfterCalcs() {
                assertEquals(26, game.getPlayers().get(0).getVictoryPoints());
            }

            /**
             * Checks the total score achieved by Player 2.
             */
            @RepeatedTest(value = 10)
            void ptsDamianoAfterCalcs() {
                assertEquals(19, game.getPlayers().get(1).getVictoryPoints());
            }

            /**
             * Checks the total score achieved by Player 3.
             */
            @RepeatedTest(value = 10)
            void ptsMarcoAfterCalcs() {
                assertEquals(11, game.getPlayers().get(2).getVictoryPoints());
            }

            /**
             * Ensures that the player with the highest points is the winner.
             */
            @RepeatedTest(value = 10)
            void isAlessandroWinner() {
                assertTrue(game.getPlayers().get(0).isWinner());
            }

            /**
             * Ensures that any player without the highest points is not the winner.
             */
            @RepeatedTest(value = 10)
            void isDamianoNotWinner() {
                assertFalse(game.getPlayers().get(1).isWinner());
            }
        }
    }

    /**
     * Nested class for tests regarding current player switches.
     */
    @Nested
    @DisplayName("Player turn switch tests")
    class PlayerSwitchTest {
        /**
         * Checks the functioning of player switch with one (out of three) inactive player.
         */
        @Test
        void onTurnEndWithOneInactivePlayer() {
            String next = game.getPlayers().get(2).getNickname();
            game.getPlayers().get(1).setActive(false);
            game.onTurnEnd();
            assertEquals(next, game.getCurrentPlayer().getNickname());
        }

        /**
         * Checks the functioning of player switch with two (out of three) inactive players.
         */
        @Test
        void onTurnEndWithTwoInactivePlayers() {
            game.getPlayers().get(1).setActive(false);
            game.getPlayers().get(2).setActive(false);
            String next = game.getPlayers().get(0).getNickname();
            game.onTurnEnd();
            game.onTurnEnd();
            game.onTurnEnd();
            assertEquals(next, game.getCurrentPlayer().getNickname());
        }

        /**
         * Checks the Game behavior if current player disconnects before ending their own turn.
         */
        @Test
        void currentPlayerDisconnects() {
            Player expected = game.getPlayers().get(1);
            game.getPlayers().get(0).setActive(false);
            game.onTurnEnd();
            Player next = game.getCurrentPlayer();
            assertEquals(expected, next);
        }
    }
}
