package it.polimi.ingsw.model;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.JavaDevCardColorFactory;
import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of Player operations.
 */
public class PlayerTest {
    ResourceTypeFactory resTypeFactory;
    DevCardColorFactory devCardColorFactory;
    Game game;
    Player player;

    /**
     * Sets up initial conditions by initializing Game and Player.
     */
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
        devCardColorFactory = new JavaDevCardColorFactory();
        game = new FileGameFactory("src/main/resources/config.xml").buildMultiGame(List.of("Alessandro","Damiano","Marco"));
        player = game.getPlayers().get(0);
    }

    /**
     * Checks that all resources in a strongbox are counted correctly.
     */
    @Test
    void getNumOfResourcesTestStrongboxOnly() {
        try {
            for(int i = 0; i < 4; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Coin"));
            for(int i = 0; i < 7; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Servant"));
            player.getStrongbox().addResource(resTypeFactory.get("Shield"));
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
                player.getStrongbox().addResource(resTypeFactory.get("Coin"));
            for(int i = 0; i < 7; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Servant"));
            for(int i = 0; i < 2; i++)
                player.getWarehouse().getShelves().get(1).addResource(resTypeFactory.get("Stone"));

            player.getWarehouse().getShelves().get(0).addResource(resTypeFactory.get("Shield"));
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
                    put(resTypeFactory.get("Coin"), 3);
                }});
                put(player.getWarehouse().getShelves().get(1), new HashMap<>() {{
                    put(resTypeFactory.get("Stone"), 2);
                }});
            }};
            try {
                for (int i = 0; i < 4; i++)
                    player.getStrongbox().addResource(resTypeFactory.get("Coin"));
                for (int i = 0; i < 7; i++)
                    player.getStrongbox().addResource(resTypeFactory.get("Servant"));
                for (int i = 0; i < 2; i++)
                    player.getWarehouse().getShelves().get(1).addResource(resTypeFactory.get("Stone"));

                player.getWarehouse().getShelves().get(0).addResource(resTypeFactory.get("Shield"));

                player.addToDevSlot(game, 1, new DevelopmentCard(devCardColorFactory.get("Blue"), 1,
                                new ResourceRequirement(
                                        new HashMap<>() {{
                                            put(resTypeFactory.get("Coin"), 3);
                                            put(resTypeFactory.get("Stone"), 2);
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
            assertEquals(1, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Coin")));
        }
        /**
         * Checks amount of servants left.
         */
        @Test
        void addToDevSlotStrongboxServants() {
            assertEquals(7, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Servant")));
        }
        /**
         * Checks amount of stones left.
         */
        @Test
        void addToDevSlotWarehouseShelfStones() {
            assertEquals(0, player.getWarehouse().getShelves().get(0).getResourceQuantity(resTypeFactory.get("Stone")));
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
    void discardLeaderTest(int marker) throws AlreadyActiveException {
        for (int i = 0; i < marker; i++)
            player.incrementFaithPoints(game);

        player.discardLeader(game, 0);
        assertEquals(marker+1, player.getFaithPoints());

    }

}
