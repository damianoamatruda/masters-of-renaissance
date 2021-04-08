package it.polimi.ingsw.model.devcardcolors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of color Green.
 */
public class BlueTest {
    /**
     * Checks name.
     */
    @Test
    public void getName(){
        assertEquals("Blue", new Blue().getName());
    }
}