package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for DevelopmentCard.
 */
public class DevelopmentCardTest {
    private final DevCardColor blue = new DevCardColor("Blue");
    private final ResourceType coin = new ResourceType("Coin", true);
    private DevelopmentCard card;
    private Player player;
    private Game game;

    /**
     * Test setup to be executed before each test.
     */
    @BeforeEach
    void setup() {
        card = new DevelopmentCard(blue, 1, new ResourceRequirement(Map.of(coin, 1)), null, 0);
        player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0, Set.of());
        game = new Game(List.of(player), List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(coin, 1), 1, coin), new FaithTrack(Set.of(), Set.of()), 0, 0);
    }

    /**
     * Tests whether taking the resources specified in the card's requirements works if the player satisfies them.
     */
    @Test
    void takeFromPlayer() {
        player.getStrongbox().addResource(coin);

        assertDoesNotThrow(() -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(coin, 1))));
    }

    /**
     * Tests whether taking the resources specified in the card's requirements fails if the player does not have enough
     * of the correct type.
     */
    @Test
    void takeFromPlayerNotEnoughRes() {
        player.getStrongbox().addResource(new ResourceType("Shield", true));

        assertThrows(Exception.class, () -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(coin, 1))));
    }
}
