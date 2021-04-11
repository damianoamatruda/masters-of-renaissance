package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;

import java.util.ArrayList;
import java.util.Map;

/**
 * Test of properties of ProductionLeader.
 */
public class ProductionLeaderTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Tests a production leader initialized with a null production.
     */
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(0, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNull(leader.getProduction());
    }

    /**
     * Tests a production leader initialized with a non-null production.
     */
    @Test
    void production() {
        Production prod = new Production(null, 0, null, 0);
        ProductionLeader leader = new ProductionLeader(0, 0, prod, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertEquals(prod, leader.getProduction());
    }
}
