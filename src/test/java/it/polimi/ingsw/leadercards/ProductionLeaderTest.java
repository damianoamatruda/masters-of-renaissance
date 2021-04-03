package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.Production;
import it.polimi.ingsw.resourcetypes.Coin;

/**
 * Test of properties of ProductionLeader
 */
public class ProductionLeaderTest {
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(null, Coin.getInstance(), null, 0);

        assertNull(leader.getProduction());
    }

    @Test
    void production() {
        Production p = new Production(null, null, false);
        ProductionLeader leader = new ProductionLeader(p, Coin.getInstance(), null, 0);

        assertEquals(p, leader.getProduction());
    }
}
