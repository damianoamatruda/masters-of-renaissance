package it.polimi.ingsw.model.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Green.
 */
public class BlueTest {
    /** Check name. */
    @Test
    public void getName(){
        assertEquals("Blue", new Blue().getName());
    }
}
