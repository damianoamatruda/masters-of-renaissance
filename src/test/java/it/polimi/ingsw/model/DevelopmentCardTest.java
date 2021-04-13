package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for DevelopmentCard.
 */
public class DevelopmentCardTest {
    private DevCardColor blue = new DevCardColor("blue");
    private ResourceType coin = new ResourceType("coin", true);
    private DevelopmentCard card;
    private Player player;
    private Game game;

    /**
     * Test setup to be executed before each test.
     */
    @BeforeEach
    void setup() {
        card = new DevelopmentCard(blue, 1, new ResourceRequirement(Map.of(coin, 1)), null, 0);
        player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
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
     * Tests whether taking the resources specified in the card's requirements fails if the player does not have enough of the correct type.
     */
    @Test
    void takeFromPlayerNotEnoughRes() {
        player.getStrongbox().addResource(new ResourceType("shiled", true));

        assertThrows(Exception.class, () -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(coin, 1))));
    }
}
