package it.polimi.ingsw.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Purple
 */
class PurpleTest {
    /** Check name. */
    @Test
    public void testName() {
        assertEquals("Purple", new Purple().getName());
    }
}
