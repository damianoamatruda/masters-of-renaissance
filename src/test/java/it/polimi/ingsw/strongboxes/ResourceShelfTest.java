package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.resourcetypes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ResourceShelf class.
 */
public class ResourceShelfTest {
    /**
     * Test the quantity of resources of a type.
     */
    @Test
    public void testQuantity1() {
        ResourceType c = Coin.getInstance();
        ResourceShelf s = new ResourceShelf(c, 3);

        assertTrue(s.isEmpty());
        assertFalse(s.isFull());
        assertNull(s.getResType());
        assertEquals(c, s.getBoundedResType());
        assertEquals(0, s.getQuantity());
    }

    /**
     * Test the quantity of resources of a type.
     */
    @Test
    public void testQuantity2() {
        ResourceType c = Coin.getInstance();
        ResourceShelf s = new ResourceShelf(c, 3);

        try {
            s.addResource(c);
            s.addResource(c);
            s.addResource(c);
            s.removeResource(c);
            s.removeResource(c);
            s.removeResource(c);
        } catch (Exception e) {
            fail();
        }

        assertTrue(s.isEmpty());
        assertFalse(s.isFull());
        assertNull(s.getResType());
        assertEquals(c, s.getBoundedResType());
        assertEquals(0, s.getQuantity());
    }

    /**
     * Test by adding a resource multiple times and getting it.
     */
    @Test
    public void testAddGet() {
        Shelf s = new Shelf(3);
        ResourceType c = Coin.getInstance();

        try {
            s.addResource(c);
            s.addResource(c);
            s.addResource(c);
        } catch (Exception e) {
            fail();
        }

        assertFalse(s.isEmpty());
        assertTrue(s.isFull());
        assertEquals(c, s.getResType());
        assertEquals(3, s.getQuantity());
    }
}
