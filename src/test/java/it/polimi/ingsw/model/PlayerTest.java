package it.polimi.ingsw.model;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of Player operations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerTest {
    Game game;
    Player player;

    /**
     * Sets up initial conditions by initializing Game and Player.
     */
    @BeforeAll
    void setup() {
        game = game = new GameFactory().buildMultiGame(List.of("Alessandro","Damiano","Marco"));
        player = game.getPlayers().get(0);
    }

    /**
     * Checks that all resources in a strongbox are counted correctly.
     */
    @Test
    void getNumOfResourcesTestStrongboxOnly() {
        try {
            for(int i = 0; i < 4; i++)
                Coin.getInstance().addIntoContainer(player.getStrongbox());
            for(int i = 0; i < 7; i++)
                Servant.getInstance().addIntoContainer(player.getStrongbox());
            Shield.getInstance().addIntoContainer(player.getStrongbox());
            assertEquals(12, player.getResourcesCount());
        }
        catch (Exception e){
            fail("Exception has been thrown");
        }
    }

    /**
     * Checks that stored resources are counted correctly.
     */
    @Test
    void getNumOfResourcesTestAnyStorage() {
        try {
            for(int i = 0; i < 4; i++)
                Coin.getInstance().addIntoContainer(player.getStrongbox());
            for(int i = 0; i < 7; i++)
                Servant.getInstance().addIntoContainer(player.getStrongbox());
            for(int i = 0; i < 2; i++)
                Stone.getInstance().addIntoContainer(player.getWarehouse().getShelves().get(1));

            Shield.getInstance().addIntoContainer(player.getWarehouse().getShelves().get(0));
            assertEquals(14, player.getResourcesCount());
        }
        catch (Exception e){
            fail("Exception has been thrown");
        }

    }

    /**
     * Generic test nested class for card deposit in normal conditions (no last turn).
     */
    @Nested
    @DisplayName("Add development card to a slot")
    class AddToDevSlotTest{
        /**
         * Prepares stored resources and resources to pay.
         */
        @BeforeEach
        void prepareResources() {
            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Coin.getInstance(), 3);
                }});
                put(player.getWarehouse().getShelves().get(1), new HashMap<>() {{
                    put(Stone.getInstance(), 2);
                }});
            }};
            try {
                for (int i = 0; i < 4; i++)
                    Coin.getInstance().addIntoContainer(player.getStrongbox());
                for (int i = 0; i < 7; i++)
                    Servant.getInstance().addIntoContainer(player.getStrongbox());
                for (int i = 0; i < 2; i++)
                    Stone.getInstance().addIntoContainer(player.getWarehouse().getShelves().get(1));

                Shield.getInstance().addIntoContainer(player.getWarehouse().getShelves().get(0));

                player.addToDevSlot(game, 1, new DevelopmentCard(Blue.getInstance(), 1,
                                new ResourceRequirement(
                                        new HashMap<>() {{
                                            put(Coin.getInstance(), 3);
                                            put(Stone.getInstance(), 2);
                                        }}), null, 2),
                        resContainers);

            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception has been thrown");
            }
        }

        /**
         * Checks amount of coins left.
         */
        @Test
        void addToDevSlotStrongboxCoins() {
            assertEquals(1, player.getStrongbox().getResourceQuantity(Coin.getInstance()));
        }
        /**
         * Checks amount of servants left.
         */
        @Test
        void addToDevSlotStrongboxServants() {
            assertEquals(7, player.getStrongbox().getResourceQuantity(Servant.getInstance()));
        }
        /**
         * Checks amount of stones left.
         */
        @Test
        void addToDevSlotWarehouseShelfStones() {
            assertEquals(0, player.getWarehouse().getShelves().get(0).getResourceQuantity(Stone.getInstance()));
        }
         /**
          * Checks value of card obtained.
          */
         @Test
         void checkCardPointsValue() {
             DevelopmentCard card = player.getDevSlots().get(1).peek();
             assertEquals(2, card.getVictoryPoints());
         }
    }

    /**
     * Checks that incrementFaithPoints() always increments only by one.
     *
     * @param marker    faith points before the tested operation
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 7, 16, 23})
    void incrementFaithPointsTest(int marker){
        for (int i = 0; i < marker+1; i++)
            player.incrementFaithPoints(game);
        assertEquals(marker+1, player.getFaithPoints());
    }

    /**
     * Checks that discarding an inactive leader card always advances current player faith marker by one.
     *
     * @param marker    faith points before the tested operation
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 7, 16, 23})
    void discardLeaderTest(int marker){
        for (int i = 0; i < marker; i++)
            player.incrementFaithPoints(game);
        try {
            player.discardLeader(game, 0);
            assertEquals(marker+1, player.getFaithPoints());
        }
        catch (Exception e){
            fail("Exception has been thrown");
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
     * Restores state to initial conditions, so that every single test is not influenced by other tests.
     */
    @AfterEach
    void restore() {
        setup();
    }
}
