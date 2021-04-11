package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test of the functionalities of the class 'LeaderCard'.
 */
public class LeaderCardTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Tests the fact that a LeaderCard cannot be created without a binding resource.
     */
    @Test
    void nullResourceCreation() {
        assertThrows(AssertionError.class, () -> new ZeroLeader(0,0,null, null, null,0));
    }

    /**
     * Tests the card's activation in case it doesn't have any requirements.
     */
    @Test
    void activateNoRequirements() {
        LeaderCard leader = new ZeroLeader(0,0,null,resTypeFactory.get("Coin"), null, 0);

        assertDoesNotThrow(() -> leader.activate(null));
        assertTrue(leader.isActive());
    }

    /**
     * Tests that the card results activated when its requirements are met by the player that owns the card.
     */
    @Test
    void activateWithRequirements() {
        LeaderCard leader = new ZeroLeader(0,0,null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        p.getStrongbox().addResource(resTypeFactory.get("Coin"));

        assertDoesNotThrow(() -> leader.activate(p));
        assertTrue(leader.isActive());
    }

    /**
     * Tests the card's activation in case the player who owns it doesn't meet the resource requirements.
     */
    @Test
    void activateWrongResources() {
        LeaderCard leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        p.getStrongbox().addResource(resTypeFactory.get("Shield"));

        assertThrows(Exception.class, () -> leader.activate(p));
        assertTrue(!leader.isActive());
    }

    /**
     * Tests the card's ability to expose its binding resource.
     */
    @Test
    void getResource() {
        LeaderCard leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertEquals(resTypeFactory.get("Coin"), leader.getResource());
    }

    /**
     * Tests whether the leader card returns null when asked for its depot.
     */
    @Test
    void getDepot() {
        LeaderCard leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertNull(leader.getDepot());
    }

    /**
     * Tests the card's ability to expose its cost.
     */
    @Test
    void getDevCardCost() {
        LeaderCard leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Map<ResourceType, Integer> cost = Map.of(resTypeFactory.get("Coin"), 1);

        assertTrue(cost.equals(leader.getDevCardCost(new HashMap<>(cost))));
    }

    /**
     * Tests whether the leader card returns null when asked for its production.
     */
    @Test
    void getProduction() {
        LeaderCard leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertNull(leader.getProduction());
    }

    /**
     * Tests the card's ability to be transparent to the replacement of market resources.
     */
    @Test
    void replaceMarketResources() {
        LeaderCard leader = new DepotLeader(0, 0, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Map<ResourceType, Integer> res = Map.of(resTypeFactory.get("Zero"), 1);

        assertTrue(res.equals(leader.replaceMarketResources(resTypeFactory.get("Zero"), new HashMap<>(res), null)));
    }
}
