package it.polimi.ingsw.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Green
 */
public class GreenTest {
    /** Check name. */
    @Test
    public void getName(){
        assertEquals("Green", new Green().getName());
    }
}
