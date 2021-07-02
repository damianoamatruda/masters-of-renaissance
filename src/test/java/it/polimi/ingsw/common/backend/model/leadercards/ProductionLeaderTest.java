package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.PlayerSetup;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of ProductionLeader.
 */
public class ProductionLeaderTest {
    private final ResourceType coin = new ResourceType("Coin", "", true);

    /**
     * Tests a production leader initialized with a non-null production.
     */
    @Test
    void production() {
        ResourceTransactionRecipe prod = new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0);
        ProductionLeader leader = new ProductionLeader(prod, coin, null, 0, 0);

        Player p = new Player("", false, List.of(leader), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        
        assertDoesNotThrow(() -> leader.activate(p));

        assertTrue(leader.getProduction(false).isPresent());
        assertEquals(prod, leader.getProduction(false).get());
    }
}
