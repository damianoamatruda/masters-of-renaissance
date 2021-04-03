package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import it.polimi.ingsw.resourcetypes.*;

/**
 * Test of properties of ZeroLeader
 */
public class ZeroLeaderTest {
    @Test
    void nullMaps() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);

        assertNull(leader.processZeros(null, null));
    }

    @Test
    void emptyMaps() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);

        assertTrue(new HashMap<>().equals(leader.processZeros(new HashMap<>(), new HashMap<>())));
    }

    /**
     * Tests the case in which there's nothing to convert.
     */
    @Test
    void otherResourceInput() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);

        Map<ResourceType, Integer>  toProcess = Map.of(Coin.getInstance(), 1),  // nothing to convert (no Zero res)
                                    zeros = Map.of(Zero.getInstance(), 1);      // and choice != this leader (different bound res)
        
        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(Map.of(Zero.getInstance(), 1)));

        zeros = Map.of(Coin.getInstance(), 1);                                  // choose this leader (res ok), still nothing to convert from
        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(Map.of(Coin.getInstance(), 1)));                // same results, processing changes nothing
    }

    @Test
    void normalUse() {
        ZeroLeader leader = new ZeroLeader(Coin.getInstance(), null, 0);
        
        Map<ResourceType, Integer>  toProcess = Map.of(Zero.getInstance(), 1),
                                    zeros = new HashMap<>(Map.of(Coin.getInstance(), 1));

        assertTrue(leader.processZeros(toProcess, zeros).equals(Map.of(Coin.getInstance(), 1)));
        assertTrue(zeros.equals(new HashMap<>()));
    }
}
