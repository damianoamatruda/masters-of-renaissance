package it.polimi.ingsw.model.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Coin.
 */
public class CoinTest {
    /**
     * Checks name.
     */
    @Test
    public void getName() {
        assertEquals("Coin", Coin.getInstance().getName());
    }
}
