package it.polimi.ingsw.model;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.devcardcolors.Green;
import it.polimi.ingsw.model.devcardcolors.Purple;
import it.polimi.ingsw.model.devcardcolors.Yellow;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of base game operations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameTest {

    Game game;

    /**
     * First instantiation of Game with 3 players.
     */
    @BeforeAll
    public void setup() {
        game = new GameFactory().buildMultiGame(List.of("Alessandro","Damiano","Marco"));
    }

    /**
     * Checks some basic instantiation effects, more specifically if player 1 has inkwell and if initial turn number is
     * 1.
     */
    @Test
    @DisplayName("Game should already be instantiated")
    void instantiationTest() {
        assertAll(() -> assertEquals(game.getTurns(),1),
                  () -> assertEquals(true, game.getPlayers().get(0).hasInkwell()));
    }

    /**
     * Checks correct instantiation of devGrid with original rules.
     *
     * @param level level of the cards to test
     */
    // TODO: Make new class 'DevCardGridTest' with tests like this
    // TODO: Don't access protected attributes
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    @Disabled("Only works with card set from the original game")
    void devGridTest(int level){
        assertAll(() -> assertEquals(4, game.devCardGrid.grid.get(Blue.getInstance()).get(level).size()),
                () -> assertEquals(4, game.devCardGrid.grid.get(Green.getInstance()).get(level).size()),
                () -> assertEquals(4, game.devCardGrid.grid.get(Purple.getInstance()).get(level).size()),
                () -> assertEquals(4, game.devCardGrid.grid.get(Yellow.getInstance()).get(level).size()));
    }

    /**
     * Basic test on peek of what cards can be purchased during the current turn.
     */
    @Disabled("Not yet implemented.") // TODO
    @Test
    void peekCardsTest() {

    }

    /**
     * Basic test on development card purchase.
     */
    @Disabled("Not yet implemented.") // TODO
    @Test
    void buyCardTest() {

    }

    /**
     * Checks that first advance does not give any points.
     */
    @Test
    void firstadvanceNoPts() {
        game.getPlayers().get(0).incrementFaithPoints(game);
        assertEquals(0, game.getPlayers().get(0).getVictoryPoints());
    }

    /**
     * Nested class for first Vatican Report related tests.
     */
    @Nested
    @DisplayName("First Vatican Report Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FirstVaticanReport {
        /**
         * Simulates a possible scenario after triggering 1st Vatican Report.
         * Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 5;</li>
         *     <li>Player 2: 8;</li>
         *     <li>Player 3: 0.</li>
         * </ol>
         */
        @BeforeEach
        public void advancePlayers() {
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints(game);
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(1).incrementFaithPoints(game);
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
            assertFalse(game.hasEnded());
        }
    }

    /**
     * Nested class for second Vatican Report related tests.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Second Vatican Report Tests")
    class SecondVaticanReport {
        /**
         * Simulates a possible scenario after triggering 2nd Vatican Report.
         * Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 5;</li>
         *     <li>Player 2: 16;</li>
         *     <li>Player 3: 8.</li>
         * </ol>
         */
        @BeforeEach
        public void advancePlayers() {
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints(game);
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints(game);

            for (int i = 0; i < 16; i++)
                game.getPlayers().get(1).incrementFaithPoints(game);
        }
        /**
         * Checks the correct progressive amount of points earned so far by Player 1 (yellow tiles points excluded).
         */
        @Test
        void secondVaticanReportPtsAlessandro() {
            assertEquals(2, game.getPlayers().get(0).getVictoryPoints());
        }

        /**
         * Checks the correct progressive amount of points earned so far by Player 2 (yellow tiles points excluded).
         */
        @Test
        void secondVaticanReportPtsDamiano() {
            assertEquals(3, game.getPlayers().get(1).getVictoryPoints());
        }

        /**
         * Checks the correct progressive amount of points earned so far by Player 3 (yellow tiles points excluded).
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Last Vatican Report and Late Game Tests")
    class LastVaticanReport {
        /**
         * Simulates a possible scenario after triggering 1st Vatican Report.
         * Current tiles reached by the 3 markers:
         * <ol>
         *     <li>Player 1: 24;</li>
         *     <li>Player 2: 20;</li>
         *     <li>Player 3: 16.</li>
         * </ol>
         */
        @BeforeEach
        public void advancePlayers() {
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints(game);
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints(game);

            for (int i = 0; i < 16; i++)
                game.getPlayers().get(1).incrementFaithPoints(game);

            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints(game);
            for (int i = 0; i < 4; i++)
                game.getPlayers().get(1).incrementFaithPoints(game);
            for (int i = 0; i < 19; i++)
                game.getPlayers().get(0).incrementFaithPoints(game);
        }

        /**
         * Ensures that the previously executed methods in advancePlayers() have not had any side effects on the order
         * of the players.
         */
        @Test
        @Disabled("Random 1st player is yet to be handled") // TODO
        void arePlayersStillInSameOrder() {
            assertAll(()->assertEquals(game.getPlayers().get(0).getNickname(),"Alessandro"),
                    ()->assertEquals(game.getPlayers().get(1).getNickname(),"Damiano"),
                    ()->assertEquals(game.getPlayers().get(2).getNickname(),"Marco"));
        }
        /**
         * Ensures that the previously executed methods in advancePlayers() have not had any side effects on the number
         * of resources stored.
         */
        @Test
        void doPlayersHaveStorableResources() {
            assertAll(()->assertEquals(game.getPlayers().get(0).getResourcesCount(),0),
                    ()->assertEquals(game.getPlayers().get(1).getResourcesCount(),0),
                    ()->assertEquals(game.getPlayers().get(2).getResourcesCount(),0));
        }

        /**
         * Nested class for 3rd Vatican section tests before calling hasEnded().
         */
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Last Vatican Report tests before deciding winner")
        class LateGameBeforeWinnerCalcs {
            /**
             * Checks the correct progressive amount of points earned so far by Player 2 (yellow tiles points excluded).
             */
            @Test
            void lastVaticanReportPtsObtained() {
                assertEquals(7, game.getPlayers().get(1).getVictoryPoints());
            }

            /**
             * Checks the correct progressive amount of points earned so far by Player 1 (yellow tiles points excluded).
             */
            @Test
            void lastVaticanReportPtsObtainedLead() {
                assertEquals(6, game.getPlayers().get(0).getVictoryPoints());
            }

            /**
             * Checks the correct progressive amount of points earned so far by Player 3 (yellow tiles points excluded).
             */
            @Test
            void lastVaticanReportPtsNotObtained() {
                assertEquals(2, game.getPlayers().get(2).getVictoryPoints());
            }
        }

        /**
         * Nested class for 3rd Vatican section tests after calling hasEnded().
         */
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Last Vatican Report tests after deciding winner")
        class LateGameAfterWinnerCalcs {
            /**
             * Closes the Game (and does the last calcs to determine winner) before the following tests.
             */
            @BeforeEach
            void endGame() {
                game.hasEnded();
            }

            /**
             * Ensures the Game has actually ended.
             */
            @Test
            void hasTheGameEnded() {
                assertTrue(game.hasEnded());
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
        @Disabled("Random 1st player is yet to be handled") // TODO
        void onTurnEndWithOneInactivePlayer() {
            game.getPlayers().get(1).setActive(false);
            assertEquals("Marco", game.onTurnEnd().getNickname());
        }

        /**
         * Checks the functioning of player switch with two (out of three) inactive players.
         */
        @Test
        @Disabled("Random 1st player is yet to be handled") // TODO
        void onTurnEndWithTwoInactivePlayers() {
            game.getPlayers().get(0).setActive(false);
            game.getPlayers().get(1).setActive(false);
            Player nowPlaying;
            nowPlaying = game.onTurnEnd();
            nowPlaying = game.onTurnEnd();
            nowPlaying = game.onTurnEnd();
            assertEquals("Marco", nowPlaying.getNickname());
        }

        /**
         * Checks the Game behavior if current player disconnects before ending their own turn.
         */
        @Test
        @Disabled("Yet to be handled when current player disconnects")
        void currentPlayerDisconnects() {
            game.getPlayers().get(0).setActive(false);
            assertEquals("Damiano", game.getPlayers().get(0).getNickname());
        }

        /**
         * Checks the Game behavior if all players disconnect.
         */
        @Test
        @Disabled("Yet to be handled")
        void onTurnEndWithAllInactivePlayers() {

        }

    }

    /**
     * Tests the lack of rep exposure.
     */
    @Disabled("Not yet implemented.") // TODO
    @Test
    void noRepExposureTest() {

    }

    /**
     * Restores Game to initial conditions, so that every single test is not influenced by other tests.
     */
    @AfterEach
    void restore() {
        setup();
    }
}
