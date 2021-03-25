package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of Color Green
 */
public class GreenTest {
    /** Check for returned name with assertNotEquals */
    @Test
    public void getName(){
        assertNotEquals("Blue",new Green().getName());
    }
}
