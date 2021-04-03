package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.Coin;
import it.polimi.ingsw.resourcetypes.Servant;
import it.polimi.ingsw.resourcetypes.Shield;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of Player operations
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerTest {
    // TODO: Implement
    Game game;
    Player player;

    /**
     * Sets up initial conditions by initializing Game and Player
     */
    @BeforeAll
    void setup(){
        game = game = new Game(List.of("Alessandro","Damiano","Marco"));
        player = game.getPlayers().get(0);
    }

    /**
     * Checks that all resources in a strongbox are counted correctly
     */
    @Test
    void getNumOfResourcesTestStrongboxOnly(){
        try {
            for(int i = 0; i < 4; i++)
                Coin.getInstance().onGiven(player, player.getStrongbox());
            for(int i = 0; i < 7; i++)
                Servant.getInstance().onGiven(player, player.getStrongbox());
            Shield.getInstance().onGiven(player, player.getStrongbox());
            assertEquals(12, player.getNumOfResources());
        }
        catch (Exception e){
            fail("Exception has been thrown");
        }
    }

    @Test
    void getNumOfResourcesTestAnyStorage(){

    }
    @Test
    void addToDevSlotTest(){

    }

    /**
     * Checks that incrementFaithPoints() always increments only by one
     * @param marker faith points before the tested operation
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 7, 16, 23})
    void incrementFaithPointsTest(int marker){
        for (int i = 0; i < marker+1; i++)
            player.incrementFaithPoints();
        assertEquals(marker+1, player.getFaithPoints());
    }

    /**
     * Checks that discarding an inactive leader card always advances current player faith marker by one
     * @param marker faith points before the tested operation
     */
    @ParameterizedTest
    @Disabled("Leader cards distribution is yet to be implemented")
    @ValueSource(ints = {0, 7, 16, 23})
    void discardLeaderTest(int marker){
        for (int i = 0; i < marker; i++)
            player.incrementFaithPoints();
        try {
            player.discardLeader(0);
            assertEquals(marker+1, player.getFaithPoints());
        }
        catch (Exception e){
            fail("Exception has been thrown");
        }
    }
    @Test
    void noRepExposureTest(){

    }
    /**
     * Restores state to initial conditions, so that every single test is not influenced by other tests
     */
    @AfterEach
    void restore(){
        setup();
    }
}
