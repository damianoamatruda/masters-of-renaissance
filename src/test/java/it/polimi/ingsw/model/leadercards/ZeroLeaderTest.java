package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of ZeroLeader.
 */
public class ZeroLeaderTest {
    private final ResourceType coin = new ResourceType("coin", true);
    private final ResourceType zero = new ResourceType("zero", false);
    private ZeroLeader leader;
    private Player p;

    @BeforeEach
    void setup() {
        leader = new ZeroLeader(0, 0, null, coin, null, 0);
        p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
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

        assertTrue(new HashMap<>().equals(leader.replaceMarketResources(zero, new HashMap<>(), new HashMap<>())));
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

        assertTrue(leader.replaceMarketResources(zero, toProcess, zeros).equals(Map.of(coin, 1)));
        assertTrue(zeros.equals(Map.of(zero, 1)));

        zeros = Map.of(coin, 1);                                  // choose this leader (res ok), still nothing to convert from
        assertTrue(leader.replaceMarketResources(zero, toProcess, zeros).equals(Map.of(coin, 1)));
        assertTrue(zeros.equals(Map.of(coin, 1)));                // same results, processing changes nothing
    }

    /**
     * Tests the zero leader converting resources properly.
     */
    @Test
    void normalUse() {
        assertDoesNotThrow(() -> leader.activate(p));

        Map<ResourceType, Integer> toProcess = Map.of(zero, 1),
                zeros = new HashMap<>(Map.of(coin, 1));

        assertTrue(leader.replaceMarketResources(zero, toProcess, zeros).equals(Map.of(coin, 1)));
        assertTrue(zeros.equals(new HashMap<>()));
    }
}
