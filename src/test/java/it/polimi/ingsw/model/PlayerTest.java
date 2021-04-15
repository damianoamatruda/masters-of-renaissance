package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardrequirements.RequirementsNotMetException;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.leadercards.DepotLeader;
import it.polimi.ingsw.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of Player operations.
 */
public class PlayerTest {
    Game game;
    Player player;

    ResourceType coin = new ResourceType("Coin", true);
    ResourceType faith = new ResourceType("Faith", false);
    ResourceType servant = new ResourceType("Servant", true);
    ResourceType shield = new ResourceType("Shield", true);
    ResourceType stone = new ResourceType("Stone", true);
    ResourceType zero = new ResourceType("Zero", false);

    DevCardColor blue = new DevCardColor("Blue");

    /**
     * Sets up initial conditions by initializing Game and Player.
     */
    @BeforeEach
    void setup() {
        List<String> shuffledNicknames = new ArrayList<>(List.of("A","B","C","D"));
        Collections.shuffle(shuffledNicknames);

        List<Integer> bonusResources = List.of(0,1,1,2);
        List<Integer> bonusFaith = List.of(0,0,1,1);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0, new ArrayList<>(List.of(new DepotLeader(0,0,null,null,null,0))),
                    new Warehouse(3), new Strongbox(),
                    new Production(Map.of(), 2, Map.of(), 1), 3,
                    bonusResources.get(i), bonusFaith.get(i));
            players.add(player);
        }

        game = new Game(
                players,
                new DevCardGrid(List.of(), 3, 4),
                new Market(Map.of(zero,4,faith,1,coin,2,servant,2,shield,2,stone,2), 4, zero),
                new FaithTrack(Set.of(), Set.of()), 24,
                7
        );
        player = game.getPlayers().get(0);
    }

    /**
     * Checks that all resources in a strongbox are counted correctly.
     */
    @Test
    void getNumOfResourcesTestStrongboxOnly() {
        for(int i = 0; i < 4; i++)
            player.getStrongbox().addResource(coin);
        for(int i = 0; i < 7; i++)
            player.getStrongbox().addResource(servant);
        player.getStrongbox().addResource(shield);
        assertEquals(12, player.getResourcesCount());
    }

    /**
     * Checks that stored resources are counted correctly.
     */
    @Test
    void getNumOfResourcesTestAnyStorage() throws IllegalResourceTransferException {
        for(int i = 0; i < 4; i++)
            player.getStrongbox().addResource(coin);
        for(int i = 0; i < 7; i++)
            player.getStrongbox().addResource(servant);
        for(int i = 0; i < 2; i++)
            player.getWarehouse().getShelves().get(1).addResource(stone);

        player.getWarehouse().getShelves().get(0).addResource(shield);
        assertEquals(14, player.getResourcesCount());
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
        void prepareResources() throws RequirementsNotMetException, IllegalCardDepositException, IllegalResourceTransferException {
            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(coin, 3);
                }});
                put(player.getWarehouse().getShelves().get(1), new HashMap<>() {{
                    put(stone, 2);
                }});
            }};

            for (int i = 0; i < 4; i++)
                player.getStrongbox().addResource(coin);
            for (int i = 0; i < 7; i++)
                player.getStrongbox().addResource(servant);
            for (int i = 0; i < 2; i++)
                player.getWarehouse().getShelves().get(1).addResource(stone);

            player.getWarehouse().getShelves().get(0).addResource(shield);

            player.addToDevSlot(game, 1, new DevelopmentCard(blue, 1,
                            new ResourceRequirement(
                                    new HashMap<>() {{
                                        put(coin, 3);
                                        put(stone, 2);
                                    }}), null, 2),
                    resContainers);

        }

        /**
         * Checks amount of coins left.
         */
        @Test
        void addToDevSlotStrongboxCoins() {
            assertEquals(1, player.getStrongbox().getResourceQuantity(coin));
        }
        /**
         * Checks amount of servants left.
         */
        @Test
        void addToDevSlotStrongboxServants() {
            assertEquals(7, player.getStrongbox().getResourceQuantity(servant));
        }
        /**
         * Checks amount of stones left.
         */
        @Test
        void addToDevSlotWarehouseShelfStones() {
            assertEquals(0, player.getWarehouse().getShelves().get(0).getResourceQuantity(stone));
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

    /**
     * Ensures that an exception is thrown when trying to discard an activated leader card.
     *
     * @throws AlreadyActiveException   leader card is already activated
     */
    @Test
    void invalidLeaderDiscard() throws IllegalActivationException {
        player.getLeaders().get(0).activate(player);

        assertThrows(AlreadyActiveException.class, ()-> player.discardLeader(game, 0));

    }

    /**
     * Ensures that an exception is thrown when trying to deposit a card on the wrong slot.
     */
    @Test
    void cannotDepositNewCard(){
        assertThrows(IllegalCardDepositException.class, ()-> player.addToDevSlot(game, 0,  new DevelopmentCard(blue, 2,
                        new ResourceRequirement(
                                new HashMap<>()), null, 2), new HashMap<>()));
    }

    @Nested
    @DisplayName("Tests related to the players receiving resources at the beginning")
    class InitialResourcesTest {
        List<Player> players;

        @BeforeEach
        void setup(){
            players = game.getPlayers();
        }

        @Test
        void firstPlayerNoFaith(){
            assertEquals(players.get(0).getFaithPoints(), 0);
        }

        @Test
        void secondPlayerNoFaith(){
            assertEquals(players.get(1).getFaithPoints(), 0);
        }

        @Test
        void thirdPlayerOneFaith(){
            assertEquals(players.get(2).getFaithPoints(), 1);
        }

        @Test
        void fourthPlayerOneFaith(){
            assertEquals(players.get(3).getFaithPoints(), 1);
        }

        @Test
        void firstPlayerNoResources(){
            Player first = players.get(0);
            assertThrows(CannotChooseException.class, ()->first.chooseResource(null, 0));
        }

        @Test
        void secondPlayerOneResource() {
            Player second = players.get(1);
            DevelopmentCard card = new DevelopmentCard(blue, 1,
                    new ResourceRequirement(Map.of(coin, 1)), null, 0);
            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
                put(second.getWarehouse().getShelves().get(1), new HashMap<>() {{
                    put(coin, 1);
                }});
            }};

            assertAll(()->assertDoesNotThrow(()->second.chooseResource(coin, 1)),
                    ()->assertDoesNotThrow(() -> second.addToDevSlot(game, 1, card, resContainers)));

        }

        @Test
        void fourthPlayerTwoResources(){
            Player fourth = players.get(3);
            assertAll(()->assertDoesNotThrow(()->fourth.chooseResource(coin, 1)),
                    ()->assertDoesNotThrow(()->fourth.chooseResource(coin, 1)),
                    ()->assertThrows(CannotChooseException.class, ()->fourth.chooseResource(coin, 1)));
        }

        @Test
        void illegalResources(){
            Player fourth = players.get(3);
            assertAll(()->assertThrows(InvalidChoiceException.class, ()-> fourth.chooseResource(zero, 1)),
                    ()->assertThrows(InvalidChoiceException.class, ()-> fourth.chooseResource(faith, 1)));
        }

    }

}
