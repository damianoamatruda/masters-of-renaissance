package it.polimi.ingsw.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Faith
 */
public class FaithTest {
    /** Check name. */
    @Test
    public void getName() {
        assertEquals("faith", Faith.getInstance().getName());
    }
}
