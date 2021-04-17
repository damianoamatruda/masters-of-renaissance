package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of ProductionLeader.
 */
public class ProductionLeaderTest {
    private final ResourceType coin = new ResourceType("coin", true);
    private final Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);

    /**
     * Tests a production leader initialized with a null production.
     */
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(null, coin, null, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertNull(leader.getProduction());
    }

    /**
     * Tests a production leader initialized with a non-null production.
     */
    @Test
    void production() {
        Production prod = new Production(Map.of(), 0, Map.of(), 0);
        ProductionLeader leader = new ProductionLeader(prod, coin, null, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertEquals(prod, leader.getProduction());
    }
}
