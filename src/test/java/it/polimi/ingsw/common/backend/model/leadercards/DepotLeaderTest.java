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
        leader = new DepotLeader(0, new ResourceType("Coin", true), null, 0, 0);
        Player p = new Player("", false, List.of(leader), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot(false));
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        leader = new DepotLeader(1, new ResourceType("Coin", true), null, 0, 0);
        Player p = new Player("", false, List.of(leader), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));

        assertDoesNotThrow(() -> leader.activate(p));

        assertNotNull(leader.getDepot(false));
    }
}
