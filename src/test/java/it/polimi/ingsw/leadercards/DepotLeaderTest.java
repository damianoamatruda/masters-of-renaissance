package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.resourcetypes.Coin;


/**
 * Test for the DepotLeader class.
 */
public class DepotLeaderTest {
    /**
     * Creation of a DepotLeader with its binding resource set to null.
     */
    @Test
    void getNullResourceDepot() {
        DepotLeader leader = new DepotLeader(1, null, null, 0);

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with a zero-sized depot.
     */
    @Test
    void getZeroSizeDepot() {
        DepotLeader leader = new DepotLeader(0, Coin.getInstance(), null, 0);

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        DepotLeader leader = new DepotLeader(1, Coin.getInstance(), null, 0);

        assertNotNull(leader.getDepot());
    }
}
