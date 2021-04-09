package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcetypes.Coin;

import java.util.Map;


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
        Player p = new Player("", false, new Warehouse(3), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        DepotLeader leader = new DepotLeader(1, Coin.getInstance(), null, 0);
        Player p = new Player("", false, new Warehouse(3), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 3, 9);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }
}
