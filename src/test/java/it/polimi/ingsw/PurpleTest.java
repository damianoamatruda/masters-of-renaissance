package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of Color Purple
 */

class PurpleTest {
    /**
     * Check for returned name with assertEquals
     */
    @Test
    public void testName() {
        assertEquals("Purple",new Purple().getName());
    }
}
