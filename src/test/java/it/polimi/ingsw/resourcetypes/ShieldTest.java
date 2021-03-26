package it.polimi.ingsw.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Shield
 */
public class ShieldTest {
    /** Check name. */
    @Test
    public void getName() {
        assertEquals("shield", Shield.getInstance().getName());
    }
}
