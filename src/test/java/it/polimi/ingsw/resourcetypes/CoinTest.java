package it.polimi.ingsw.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Coin
 */
public class CoinTest {
    /** Check name. */
    @Test
    public void getName() {
        assertEquals("Coin", Coin.getInstance().getName());
    }
}
