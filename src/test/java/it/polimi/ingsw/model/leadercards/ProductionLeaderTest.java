package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcetypes.Coin;

import java.util.ArrayList;
import java.util.Map;

/**
 * Test of properties of ProductionLeader.
 */
public class ProductionLeaderTest {
    // TODO: Add Javadoc
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(null, Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0, 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNull(leader.getProduction());
    }

    // TODO: Add Javadoc
    @Test
    void production() {
        Production<ResourceContainer, Strongbox> prod = new Production<>(null, 0, null, 0);
        ProductionLeader leader = new ProductionLeader(prod, Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0, 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertEquals(prod, leader.getProduction());
    }
}
