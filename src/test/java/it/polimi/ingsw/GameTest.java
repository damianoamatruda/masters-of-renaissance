package it.polimi.ingsw;

import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test of base game operations */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameTest {
    // TODO: Implement

    Game game;

    @BeforeAll
    public void setup(){
        game = new Game(List.of("Alessandro","Damiano","Marco"));
    }

    @Test
    @DisplayName("Game should already be instantiated")
    void instantiationTest(){
        assertAll(() -> assertEquals(game.getTurns(),0),
                  () -> assertEquals(true, game.getPlayers().get(0).hasInkwell()));
    }

    @Test
    void peekCardsTest(){

    }
    @Test
    void buyCardTest(){

    }

    @Test
    void firstadvanceNoPts() {
        game.getPlayers().get(0).incrementFaithPoints();
        assertEquals(0, game.getPlayers().get(0).getVictoryPoints());
    }

    @Nested
    @DisplayName("First Vatican Report Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FirstVaticanReport {
        @BeforeEach
        public void advancePlayers(){
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints();
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(1).incrementFaithPoints();
        }
        @Test
        void firstVaticanReportPtsObtained() {
            assertEquals(2, game.getPlayers().get(0).getVictoryPoints());
        }
        @Test
        void firstVaticanReportPtsNotObtained() {
            assertEquals(0, game.getPlayers().get(2).getVictoryPoints());
        }
        @Test
        void hasTheGameEnded(){
            assertFalse(game.hasEnded());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Second Vatican Report Tests")
    class SecondVaticanReport {
        @BeforeEach
        public void advancePlayers(){
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints();
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints();

            for (int i = 0; i < 16; i++)
                game.getPlayers().get(1).incrementFaithPoints();
        }
        @Test
        void secondVaticanReportPtsAlessandro() {
            assertEquals(2, game.getPlayers().get(0).getVictoryPoints());
        }
        @Test
        void secondVaticanReportPtsDamiano() {
            assertEquals(3, game.getPlayers().get(1).getVictoryPoints());
        }
        @Test
        void secondVaticanReportPtsMarco() {
            assertEquals(2, game.getPlayers().get(2).getVictoryPoints());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Last Vatican Report and Late Game Tests")
    class LastVaticanReport {
        @BeforeEach
        public void advancePlayers(){
            for (int i = 0; i < 5; i++)
                game.getPlayers().get(0).incrementFaithPoints();
            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints();

            for (int i = 0; i < 16; i++)
                game.getPlayers().get(1).incrementFaithPoints();

            for (int i = 0; i < 8; i++)
                game.getPlayers().get(2).incrementFaithPoints();
            for (int i = 0; i < 4; i++)
                game.getPlayers().get(1).incrementFaithPoints();
            for (int i = 0; i < 19; i++)
                game.getPlayers().get(0).incrementFaithPoints();
        }
        @Test
        void arePlayersStillInSameOrder(){
            assertAll(()->assertEquals(game.getPlayers().get(0).getNickname(),"Alessandro"),
                    ()->assertEquals(game.getPlayers().get(1).getNickname(),"Damiano"),
                    ()->assertEquals(game.getPlayers().get(2).getNickname(),"Marco"));
        }
        @Test
        void doPlayersHaveStorableResources(){
            assertAll(()->assertEquals(game.getPlayers().get(0).getNumOfResources(),0),
                    ()->assertEquals(game.getPlayers().get(1).getNumOfResources(),0),
                    ()->assertEquals(game.getPlayers().get(2).getNumOfResources(),0));
        }
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Last Vatican Report tests before deciding winner")
        class LateGameBeforeWinnerCalcs {
            @Test
            void lastVaticanReportPtsObtained() {
                assertEquals(7, game.getPlayers().get(1).getVictoryPoints());
            }

            @Test
            void lastVaticanReportPtsObtainedLead() {
                assertEquals(6, game.getPlayers().get(0).getVictoryPoints());
            }

            @Test
            void lastVaticanReportPtsNotObtained() {
                assertEquals(2, game.getPlayers().get(2).getVictoryPoints());
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Last Vatican Report tests after deciding winner")
        class LateGameAfterWinnerCalcs {
            @BeforeEach
            void endGame(){
                game.hasEnded();
            }

            @Test
            void hasTheGameEnded() {
                assertTrue(game.hasEnded());
            }

            @Test
            void ptsAlessandroAfterCalcs(){
                assertEquals(26, game.getPlayers().get(0).getVictoryPoints());
            }
            @Test
            void ptsDamianoAfterCalcs(){
                assertEquals(19, game.getPlayers().get(1).getVictoryPoints());
            }
            @Test
            void ptsMarcoAfterCalcs(){
                assertEquals(11, game.getPlayers().get(2).getVictoryPoints());
            }
            @Test
            void isAlessandroWinner() {
                assertTrue(game.getPlayers().get(0).isWinner());
            }

            @Test
            void isDamianoNotWinner() {
                assertFalse(game.getPlayers().get(1).isWinner());
            }
        }
    }

    @Nested
    @DisplayName("Player turn switch tests")
    class PlayerSwitchTest {
        @Test
        void onTurnEndWithOneInactivePlayer() {
            game.getPlayers().get(1).setActive(false);
            assertEquals("Marco", game.onTurnEnd().getNickname());
        }

        @Test
        void onTurnEndWithTwoInactivePlayers() {
            game.getPlayers().get(0).setActive(false);
            game.getPlayers().get(1).setActive(false);
            Player nowPlaying = game.onTurnEnd();
            nowPlaying = game.onTurnEnd();
            nowPlaying = game.onTurnEnd();
            assertEquals("Marco", game.getPlayers().get(0).getNickname());
        }

        @Test
        @Disabled("Yet to be handled when current player disconnects")
        void currentPlayerDisconnects() {
            game.getPlayers().get(0).setActive(false);
            assertEquals("Damiano", game.getPlayers().get(0).getNickname());
        }

        @Test
        @Disabled("Yet to be handled")
        void onTurnEndWithAllInactivePlayers() {

        }
        @AfterEach
        void restore(){
            setup();
        }
    }
    @Test
    void hasEndedTest(){

    }
    @Test
    void noRepExposureTest(){

    }
    @AfterEach
    void restore(){
        setup();
    }
}
