package it.polimi.ingsw.server.model.leadercards;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of ProductionLeader.
 */
public class ProductionLeaderTest {
    private final ResourceType coin = new ResourceType("Coin", true);
    private final Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0, Set.of());

    /**
     * Tests a production leader initialized with a non-null production.
     */
    @Test
    void production() {
        Production prod = new Production(Map.of(), 0, Map.of(), 0);
        ProductionLeader leader = new ProductionLeader(prod, coin, null, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertTrue(leader.getProduction().isPresent());
        assertEquals(prod, leader.getProduction().get());
    }
}
