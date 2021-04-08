package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Shelf class.
 */
public class ShelfTest {
    /**
     * Tests the quantity of a resource in a shelf.
     */
    @Test
    public void testQuantity1() {
        Shelf s = new Shelf(3);

        assertTrue(s.isEmpty());
        assertFalse(s.isFull());
        assertNull(s.getResType());
        assertEquals(0, s.getQuantity());
    }

    /**
     * Tests the quantity of a resource in a shelf.
     */
    @Test
    public void testQuantity2() {
        Shelf s = new Shelf(3);
        ResourceType c = Coin.getInstance();

        try {
            for (int i = 0; i < 3; i++)
                s.addResource(c);
            for (int i = 0; i < 3; i++)
                s.removeResource(c);
        } catch (Exception e) {
            fail();
        }

        assertTrue(s.isEmpty());
        assertFalse(s.isFull());
        assertNull(s.getResType());
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
            for (int i = 0; i < 3; i++)
                s.addResource(c);
        } catch (Exception e) {
            fail();
        }

        assertFalse(s.isEmpty());
        assertTrue(s.isFull());
        assertEquals(c, s.getResType());
        assertEquals(3, s.getQuantity());
        assertEquals(s.getResourceQuantity(s.getResType()), s.getQuantity());
    }
}