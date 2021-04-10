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
    
    // TODO: Add Javadoc
    @Test
    void nullResourceCreation() {
        assertThrows(AssertionError.class, () -> new LeaderCard(null, null, 0));
    }

    // TODO: Add Javadoc
    @Test
    void activateNoRequirements() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), null, 0);

        assertDoesNotThrow(() -> leader.activate(null));
        assertTrue(leader.isActive());
    }

    // TODO: Add Javadoc
    @Test
    void activateWithRequirements() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.getStrongbox().addResource(resTypeFactory.get("Coin")); } catch (Exception e) { }

        assertDoesNotThrow(() -> leader.activate(p));
        assertTrue(leader.isActive());
    }

    // TODO: Add Javadoc
    @Test
    void activateWrongResources() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.getStrongbox().addResource(resTypeFactory.get("Shield")); } catch (Exception e) { }

        assertThrows(Exception.class, () -> leader.activate(p));
        assertTrue(!leader.isActive());
    }

    // TODO: Add Javadoc
    @Test
    void getResource() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertEquals(resTypeFactory.get("Coin"), leader.getResource());
    }

    // TODO: Add Javadoc
    @Test
    void getDepot() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertNull(leader.getDepot());
    }

    // TODO: Add Javadoc
    @Test
    void getDevCardCost() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Map<ResourceType, Integer> cost = Map.of(resTypeFactory.get("Coin"), 1);

        assertTrue(cost.equals(leader.getDevCardCost(new HashMap<>(cost))));
    }

    // TODO: Add Javadoc
    @Test
    void getProduction() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        assertNull(leader.getProduction());
    }

    // TODO: Add Javadoc
    @Test
    void processZeros() {
        LeaderCard leader = new LeaderCard(resTypeFactory.get("Coin"), new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), 0);

        Map<ResourceType, Integer> res = Map.of(resTypeFactory.get("Zero"), 1);

        assertTrue(res.equals(leader.processZeros(new HashMap<>(res), null)));
    }
}
