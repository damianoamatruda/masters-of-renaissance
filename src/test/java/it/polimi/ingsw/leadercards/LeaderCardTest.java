package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.ResourceRequirement;
import it.polimi.ingsw.resourcetypes.*;

/**
 * Test of the functionalities of the class 'LeaderCard'
 */
public class LeaderCardTest {
    @Test
    void nullResourceCreation() {
        assertThrows(AssertionError.class, () -> new LeaderCard(null, null, 0));
    }

    @Test
    void activateNoRequirements() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), null, 0);

        assertDoesNotThrow(() -> leader.activate(null));
        assertTrue(leader.isActive());
    }

    @Test
    void activateWithRequirements() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);
        
        Player p = new Player("", List.of(leader), false);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }

        assertDoesNotThrow(() -> leader.activate(p));
        assertTrue(leader.isActive());
    }

    @Test
    void activateWrongResources() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);
        
        Player p = new Player("", List.of(leader), false);
        try { p.getStrongbox().addResource(Shield.getInstance()); } catch (Exception e) { }

        assertThrows(Exception.class, () -> leader.activate(p));
        assertTrue(!leader.isActive());
    }

    @Test
    void getResource() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);

        assertEquals(Coin.getInstance(), leader.getResource());
    }

    @Test
    void getDepot() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);

        assertNull(leader.getDepot());
    }

    @Test
    void getDevCardCost() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);

        Map<ResourceType, Integer> cost = Map.of(Coin.getInstance(), 1);

        assertTrue(cost.equals(leader.getDevCardCost(new HashMap<>(cost))));
    }

    @Test
    void getProduction() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);

        assertNull(leader.getProduction());
    }

    @Test
    void processZeros() {
        LeaderCard leader = new LeaderCard(Coin.getInstance(), new ResourceRequirement(Map.of(Coin.getInstance(), 1)), 0);

        Map<ResourceType, Integer> res = Map.of(Zero.getInstance(), 1);

        assertTrue(res.equals(leader.processZeros(new HashMap<>(res), null)));
    }
}
