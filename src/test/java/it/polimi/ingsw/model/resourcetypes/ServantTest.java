package it.polimi.ingsw.model.resourcetypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of properties of resource type Servant
 */
public class ServantTest {
    /** Check name. */
    @Test
    public void getName() {
        assertEquals("Servant", Servant.getInstance().getName());
    }
}
