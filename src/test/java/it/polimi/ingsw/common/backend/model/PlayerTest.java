package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.common.backend.model.leadercards.DepotLeader;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionActivationException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    final ResourceType coin = new ResourceType("Coin", true);
    final ResourceType faith = new ResourceType("Faith", false);
    final ResourceType servant = new ResourceType("Servant", true);
    final ResourceType shield = new ResourceType("Shield", true);
    final ResourceType stone = new ResourceType("Stone", true);
    final ResourceType zero = new ResourceType("Zero", false);

    final DevCardColor blue = new DevCardColor("Blue");

    /**
     * Sets up initial conditions by initializing Game and Player.
     */
    @BeforeEach
    void setup() {
        List<String> shuffledNicknames = new ArrayList<>(List.of("A", "B", "C", "D"));
        Collections.shuffle(shuffledNicknames);

        List<Integer> bonusResources = List.of(0, 1, 1, 2);
        List<Integer> bonusFaith = List.of(0, 0, 1, 1);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0, new ArrayList<>(List.of(new DepotLeader(0, null, null, 0, 0))),
                    new Warehouse(3), new Strongbox(),
                    new ResourceTransactionRecipe(Map.of(), 2, Map.of(), 1), 3,
                    0, bonusResources.get(i), bonusFaith.get(i), Set.of());
            players.add(player);
        }

        game = new Game(
                players,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new DevCardGrid(List.of(), 3, 4), new Market(Map.of(zero, 4, faith, 1, coin, 2, servant, 2, shield, 2, stone, 2), 4, zero),
                new FaithTrack(Set.of(), Set.of()),
                24, 7);
        player = game.getPlayers().get(0);
    }

    /**
     * Checks that all resources in a strongbox are counted correctly.
     */
    @Test
    void getNumOfResourcesTestStrongboxOnly() {
        for (int i = 0; i < 4; i++)
            player.getStrongbox().addResource(coin);
        for (int i = 0; i < 7; i++)
            player.getStrongbox().addResource(servant);
        player.getStrongbox().addResource(shield);
        assertEquals(12, player.getResourcesCount());
    }

    /**
     * Checks that stored resources are counted correctly.
     */
    @Test
    void getNumOfResourcesTestAnyStorage() throws IllegalResourceTransferException {
        for (int i = 0; i < 4; i++)
            player.getStrongbox().addResource(coin);
        for (int i = 0; i < 7; i++)
            player.getStrongbox().addResource(servant);
        for (int i = 0; i < 2; i++)
            player.getWarehouse().getShelves().get(1).addResource(stone);

        player.getWarehouse().getShelves().get(0).addResource(shield);
        assertEquals(14, player.getResourcesCount());
    }

    /**
     * Checks that incrementFaithPoints() always increments only by one.
     *
     * @param marker faith points before the tested operation
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 7, 16, 23})
    void incrementFaithPointsTest(int marker) {
        player.incrementFaithPoints(game, marker + 1);
        assertEquals(marker + 1, player.getFaithPoints());
    }

    /**
     * Checks that discarding an inactive leader card always advances current player faith marker by one.
     *
     * @param marker faith points before the tested operation
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 7, 16, 23})
    void discardLeaderTest(int marker) throws ActiveLeaderDiscardException {
        player.incrementFaithPoints(game, marker);

        player.discardLeader(game, player.getLeaders().get(0));
        assertEquals(marker + 1, player.getFaithPoints());

    }

    /**
     * Ensures that an exception is thrown when trying to discard an activated leader card.
     *
     * @throws CardRequirementsNotMetException  leader cannot be activated due to requirements not met.
     * @throws IllegalArgumentException         player does not own the given card
     */
    @Test
    void invalidLeaderDiscard() throws IllegalArgumentException, CardRequirementsNotMetException {
        player.getLeaders().get(0).activate(player);

        assertThrows(ActiveLeaderDiscardException.class, () -> player.discardLeader(game, player.getLeaders().get(0)));

    }

    /**
     * Ensures that an exception is thrown when trying to deposit a card on the wrong slot.
     */
    @Test
    void cannotDepositNewCard() {
        assertThrows(IllegalCardDepositException.class, () -> player.addToDevSlot(game, 0, new DevelopmentCard(blue, 2,
                new ResourceRequirement(
                        Map.of()), null, 2, 0), Map.of()));
    }

    /**
     * Generic test nested class for card deposit in normal conditions (no last round).
     */
    @Nested
    @DisplayName("Add development card to a slot")
    class AddToDevSlotTest {
        /**
         * Prepares stored resources and resources to pay.
         */
        @BeforeEach
        void prepareResources() throws CardRequirementsNotMetException, IllegalCardDepositException, IllegalResourceTransferException {
            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = Map.of(
                    player.getStrongbox(), Map.of(coin, 3),
                    player.getWarehouse().getShelves().get(1), Map.of(stone, 2)
            );

            for (int i = 0; i < 4; i++)
                player.getStrongbox().addResource(coin);
            for (int i = 0; i < 7; i++)
                player.getStrongbox().addResource(servant);
            for (int i = 0; i < 2; i++)
                player.getWarehouse().getShelves().get(1).addResource(stone);

            player.getWarehouse().getShelves().get(0).addResource(shield);

            player.addToDevSlot(game, 1, new DevelopmentCard(blue, 1,
                            new ResourceRequirement(
                                    Map.of(
                                            coin, 3,
                                            stone, 2
                                    )
                            ), null, 2, 0),
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

    @Nested
    @DisplayName("Tests related to the players receiving resources at the beginning")
    class InitialResourcesTest {
        List<Player> players;

        @BeforeEach
        void setup() {
            players = game.getPlayers();
        }

        @Test
        void firstPlayerNoFaith() {
            assertEquals(players.get(0).getFaithPoints(), 0);
        }

        @Test
        void secondPlayerNoFaith() {
            assertEquals(players.get(1).getFaithPoints(), 0);
        }

        @Test
        void thirdPlayerOneFaith() {
            assertEquals(players.get(2).getFaithPoints(), 1);
        }

        @Test
        void fourthPlayerOneFaith() {
            assertEquals(players.get(3).getFaithPoints(), 1);
        }

        @Test
        void firstPlayerNoResources() {
            Player first = players.get(0);
            Warehouse.WarehouseShelf shelf = first.getWarehouse().getShelves().get(0);
            assertThrows(CannotChooseException.class, () -> first.chooseResources(
                    game, Map.of(shelf, Map.of(coin, 1))
            ));
        }

        @Test
        void secondPlayerOneResource() {
            Player second = players.get(1);
            DevelopmentCard card = new DevelopmentCard(blue, 1,
                    new ResourceRequirement(Map.of(coin, 1)), null, 0, 0);
            Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = Map.of(
                    second.getWarehouse().getShelves().get(1), Map.of(coin, 1)
            );

            Warehouse.WarehouseShelf shelf = second.getWarehouse().getShelves().get(1);

            assertAll(() -> assertDoesNotThrow(() -> second.chooseResources(
                    game, Map.of(shelf, Map.of(coin, 1))
                    )),
                    () -> assertDoesNotThrow(() -> second.addToDevSlot(game, 1, card, resContainers)));
        }

        @Test
        void fourthPlayerTwoResources() {
            Player fourth = players.get(3);
            Warehouse.WarehouseShelf shelf = fourth.getWarehouse().getShelves().get(1);
            assertAll(() -> assertThrows(IllegalResourceTransactionActivationException.class, () -> fourth.chooseResources(
                    game, Map.of(shelf, Map.of(coin, 3))
                    )),
                    () -> assertDoesNotThrow(() -> fourth.chooseResources(
                            game, Map.of(shelf, Map.of(coin, 2))
                    )));
        }

        @Test
        void illegalResources() {
            Player fourth = players.get(3);
            Warehouse.WarehouseShelf shelf = fourth.getWarehouse().getShelves().get(1);
            assertAll(() -> assertThrows(IllegalResourceTransactionActivationException.class, () -> fourth.chooseResources(
                    game, Map.of(shelf, Map.of(zero, 1))
                    )),
                    () -> assertThrows(IllegalResourceTransactionActivationException.class, () -> fourth.chooseResources(
                            game, Map.of(shelf, Map.of(faith, 1))
                    )));
        }

    }

}
