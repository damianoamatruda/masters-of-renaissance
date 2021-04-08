package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.Production;
import it.polimi.ingsw.resourcetypes.Coin;

/**
 * Test of properties of ProductionLeader
 */
public class ProductionLeaderTest {
    @Test
    void nullProduction() {
        ProductionLeader leader = new ProductionLeader(null, Coin.getInstance(), null, 0);
        Player p = new Player("", List.of(leader), false, 3, 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertNull(leader.getProduction());
    }

    @Test
    void production() {
        Production prod = new Production(null, null);
        ProductionLeader leader = new ProductionLeader(prod, Coin.getInstance(), null, 0);
        Player p = new Player("", List.of(leader), false, 3, 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertEquals(prod, leader.getProduction());
    }
}
