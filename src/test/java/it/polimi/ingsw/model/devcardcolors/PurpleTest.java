package it.polimi.ingsw.model.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Purple.
 */
class PurpleTest {
    /**
     * Checks name.
     */
    @Test
    public void testName() {
        assertEquals("Purple", new Purple().getName());
    }
}
