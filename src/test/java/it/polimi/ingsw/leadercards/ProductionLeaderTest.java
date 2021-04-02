package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.Production;

/**
 * Test of properties of ProductionLeader
 */
public class ProductionLeaderTest {
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(null, null, null, 0);

        assertNull(leader.getProduction());
    }

    @Test
    void production() {
        Production p = new Production(null, null, false);
        ProductionLeader leader = new ProductionLeader(p, null, null, 0);

        assertEquals(p, leader.getProduction());
    }
}
