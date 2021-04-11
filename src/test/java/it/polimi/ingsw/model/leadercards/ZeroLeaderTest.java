package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test of properties of ZeroLeader.
 */
public class ZeroLeaderTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Tests a zero leader on a null map.
     * The result should be another null map.
     */
    @Test
    void nullMaps() {
        ZeroLeader leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNull(leader.replaceMarketResources(null, null, null));
    }

    /**
     * Tests a zero leader on a non-null map that is empty.
     * The result should be another empty map.
     */
    @Test
    void emptyMaps() {
        ZeroLeader leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertTrue(new HashMap<>().equals(leader.replaceMarketResources(resTypeFactory.get("Zero"), new HashMap<>(), new HashMap<>())));
    }

    /**
     * Tests the case in which there's nothing to convert.
     * The result should be a map with the same values as the input map.
     */
    @Test
    void otherResourceInput() {
        ZeroLeader leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        Map<ResourceType, Integer>  toProcess = Map.of(resTypeFactory.get("Coin"), 1),  // nothing to convert (no Zero res)
                                    zeros = Map.of(resTypeFactory.get("Zero"), 1);      // and choice != this leader (different bound res)
        
        assertTrue(leader.replaceMarketResources(resTypeFactory.get("Zero"), toProcess, zeros).equals(Map.of(resTypeFactory.get("Coin"), 1)));
        assertTrue(zeros.equals(Map.of(resTypeFactory.get("Zero"), 1)));

        zeros = Map.of(resTypeFactory.get("Coin"), 1);                                  // choose this leader (res ok), still nothing to convert from
        assertTrue(leader.replaceMarketResources(resTypeFactory.get("Zero"), toProcess, zeros).equals(Map.of(resTypeFactory.get("Coin"), 1)));
        assertTrue(zeros.equals(Map.of(resTypeFactory.get("Coin"), 1)));                // same results, processing changes nothing
    }

    /**
     * Tests the zero leader converting resources properly.
     */
    @Test
    void normalUse() {
        ZeroLeader leader = new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }
        
        Map<ResourceType, Integer>  toProcess = Map.of(resTypeFactory.get("Zero"), 1),
                                    zeros = new HashMap<>(Map.of(resTypeFactory.get("Coin"), 1));

        assertTrue(leader.replaceMarketResources(resTypeFactory.get("Zero"), toProcess, zeros).equals(Map.of(resTypeFactory.get("Coin"), 1)));
        assertTrue(zeros.equals(new HashMap<>()));
    }
}
