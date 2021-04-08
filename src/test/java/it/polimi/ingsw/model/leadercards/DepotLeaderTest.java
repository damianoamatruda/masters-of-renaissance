package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcetypes.Coin;


/**
 * Test for the DepotLeader class.
 */
public class DepotLeaderTest {
    /**
     * Creation of a DepotLeader with a zero-sized depot.
     */
    @Test
    void getZeroSizeDepot() {
        DepotLeader leader = new DepotLeader(0, Coin.getInstance(), null, 0);
        Player p = new Player("", List.of(leader), false, 3, 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        DepotLeader leader = new DepotLeader(1, Coin.getInstance(), null, 0);
        Player p = new Player("", List.of(leader), false, 3, 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }
}
