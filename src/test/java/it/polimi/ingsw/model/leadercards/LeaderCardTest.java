package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class 'LeaderCard'.
 */
public class LeaderCardTest {
    private final ResourceType coin = new ResourceType("coin", true);
    private LeaderCard leader;

    @BeforeEach
    void setup() {
        leader = new ZeroLeader(coin, new ResourceRequirement(Map.of(coin, 1)), 0);
    }

    /**
     * Tests the fact that a LeaderCard cannot be created without a binding resource.
     */
    @Test
    void nullResourceCreation() {
        assertDoesNotThrow(() -> new ZeroLeader(null, null, 0));
    }

    /**
     * Tests the card's activation in case it doesn't have any requirements.
     */
    @Test
    void activateNoRequirements() {
        leader = new ZeroLeader(coin, null, 0);

        assertDoesNotThrow(() -> leader.activate(null));
        assertTrue(leader.isActive());
    }

    /**
     * Tests that the card results activated when its requirements are met by the player that owns the card.
     */
    @Test
    void activateWithRequirements() {
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        p.getStrongbox().addResource(coin);

        assertDoesNotThrow(() -> leader.activate(p));
        assertTrue(leader.isActive());
    }

    /**
     * Tests the card's activation in case the player who owns it doesn't meet the resource requirements.
     */
    @Test
    void activateWrongResources() {
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        p.getStrongbox().addResource(new ResourceType("shield", true));

        assertThrows(Exception.class, () -> leader.activate(p));
        assertFalse(leader.isActive());
    }

    /**
     * Tests the card's ability to expose its binding resource.
     */
    @Test
    void getResource() {
        assertEquals(coin, leader.getResource());
    }

    /**
     * Tests whether the leader card returns null when asked for its depot.
     */
    @Test
    void getDepot() {
        assertNull(leader.getDepot());
    }

    /**
     * Tests the card's ability to expose its cost.
     */
    @Test
    void getDevCardCost() {
        Map<ResourceType, Integer> cost = Map.of(coin, 1);

        assertEquals(cost, leader.getDevCardCost(new HashMap<>(cost)));
    }

    /**
     * Tests whether the leader card returns null when asked for its production.
     */
    @Test
    void getProduction() {
        assertNull(leader.getProduction());
    }

    /**
     * Tests the card's ability to be transparent to the replacement of market resources.
     */
    @Test
    void replaceMarketResources() {
        leader = new DepotLeader(0, coin, new ResourceRequirement(Map.of(coin, 1)), 0);

        Map<ResourceType, Integer> res = Map.of(new ResourceType("zero", false), 1);

        assertEquals(res, leader.replaceMarketResources(new ResourceType("zero", false), new HashMap<>(res), null));
    }
}
