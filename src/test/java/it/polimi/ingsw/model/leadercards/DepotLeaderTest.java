package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Map;


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
        leader = new DepotLeader(0, 0, null, new ResourceType("coin", true), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        leader = new DepotLeader(0, 1, null, new ResourceType("coin", true), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot());
    }
}
