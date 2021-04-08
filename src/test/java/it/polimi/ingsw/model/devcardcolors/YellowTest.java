package it.polimi.ingsw.model.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Yellow
 */
public class YellowTest {
    /** Check name. */
    @Test
    public void getName(){
        assertEquals("Yellow", new Yellow().getName());
    }
}
