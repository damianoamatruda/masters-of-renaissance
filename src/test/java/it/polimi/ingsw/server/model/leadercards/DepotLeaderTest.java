package it.polimi.ingsw.server.model.leadercards;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test for the DepotLeader class.
 */
public class DepotLeaderTest {
    private DepotLeader leader;

    /**
     * Creation of a DepotLeader with a zero-sized depot.
     */
    @Test
    void getZeroSizeDepot() {
        leader = new DepotLeader(0, new ResourceType("Coin", true), null, 0);
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        leader = new DepotLeader(1, new ResourceType("Coin", true), null, 0);
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot());
    }
}
