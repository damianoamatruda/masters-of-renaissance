package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test of properties of ZeroLeader.
 */
public class ZeroLeaderTest {
    // TODO: Add Javadoc
    @Test
    void nullMaps() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNull(leader.processZeros(null, null));
    }

    // TODO: Add Javadoc
    @Test
    void emptyMaps() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertTrue(new HashMap<>().equals(leader.processZeros(new HashMap<>(), new HashMap<>())));
    }

    /**
     * Tests the case in which there's nothing to convert.
     */
    @Test
    void otherResourceInput() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        Map<ResourceType, Integer>  toProcess = Map.of(Coin.getInstance(), 1),  // nothing to convert (no Zero res)
                                    zeros = Map.of(Zero.getInstance(), 1);      // and choice != this leader (different bound res)
        
        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(Map.of(Zero.getInstance(), 1)));

        zeros = Map.of(Coin.getInstance(), 1);                                  // choose this leader (res ok), still nothing to convert from
        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(Map.of(Coin.getInstance(), 1)));                // same results, processing changes nothing
    }

    // TODO: Add Javadoc
    @Test
    void normalUse() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }
        
        Map<ResourceType, Integer>  toProcess = Map.of(Zero.getInstance(), 1),
                                    zeros = new HashMap<>(Map.of(Coin.getInstance(), 1));

        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(new HashMap<>()));
    }
}
