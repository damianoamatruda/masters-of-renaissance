package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.PlayerSetup;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of ZeroLeader.
 */
public class ZeroLeaderTest {
    private final ResourceType coin = new ResourceType("Coin", "", true);
    private final ResourceType zero = new ResourceType("Zero", "", false);
    private ZeroLeader leader;
    private Player p;

    @BeforeEach
    void setup() {
        leader = new ZeroLeader(coin, null, 0, 0);
        p = new Player("", false, List.of(leader), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
    }

    /**
     * Tests a zero leader on a null map. The result should be another null map.
     */
    @Test
    void nullMaps() {
        assertDoesNotThrow(() -> leader.activate(p));

        assertNull(leader.replaceMarketResources(null, null, null));
    }

    /**
     * Tests a zero leader on a non-null map that is empty. The result should be another empty map.
     */
    @Test
    void emptyMaps() {
        assertDoesNotThrow(() -> leader.activate(p));

        assertEquals(Map.of(), leader.replaceMarketResources(zero, Map.of(), Map.of()));
    }

    /**
     * Tests the case in which there's nothing to convert. The result should be a map with the same values as the input
     * map.
     */
    @Test
    void otherResourceInput() {
        assertDoesNotThrow(() -> leader.activate(p));

        Map<ResourceType, Integer> toProcess = Map.of(coin, 1),  // nothing to convert (no Zero res)
                zeros = Map.of(zero, 1);      // and choice != this leader (different bound res)

        assertEquals(leader.replaceMarketResources(zero, toProcess, zeros), Map.of(coin, 1));
        assertEquals(zeros, Map.of(zero, 1));

        zeros = Map.of(coin, 1);                                  // choose this leader (res ok), still nothing to convert from
        assertEquals(leader.replaceMarketResources(zero, toProcess, zeros), Map.of(coin, 1));
        assertEquals(zeros, Map.of(coin, 1));                // same results, processing changes nothing
    }

    /**
     * Tests the zero leader converting resources properly.
     */
    @Test
    void normalUse() {
        assertDoesNotThrow(() -> leader.activate(p));

        Map<ResourceType, Integer> toProcess = Map.of(zero, 1),
                zeros = new HashMap<>(Map.of(coin, 1));

        assertEquals(leader.replaceMarketResources(zero, toProcess, zeros), Map.of(coin, 1));
        assertEquals(zeros, Map.of());
    }
}
