package it.polimi.ingsw.model.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Faith.
 */
public class FaithTest {
    /**
     * Checks name.
     */
    @Test
    public void getName() {
        assertEquals("Faith", Faith.getInstance().getName());
    }
}