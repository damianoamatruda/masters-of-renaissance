package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Warehouse and WarehouseShelf classes.
 */
public class WarehouseTest {
    /**
     * Test the size of the shelves.
     */
    @Test
    public void testShelvesSize() {
        Warehouse w = new Warehouse(3);

        assertNotNull(w.getShelves());
        assertEquals(w.getShelvesCount(), w.getShelves().size());
        for (int i = 0; i < w.getShelvesCount(); i++)
            assertEquals(i+1, w.getShelves().get(i).getSize());
    }

    /**
     * Test the content of the shelves.
     */
    @Test
    public void testShelvesContent() {
        Warehouse w = new Warehouse(3);
        ResourceType c = Coin.getInstance();
        ResourceType s = Servant.getInstance();

        try {
            /* Shelf 1 */
            w.getShelves().get(1).addResource(c);

            /* Shelf 2 */
            w.getShelves().get(2).addResource(s);
            w.getShelves().get(2).addResource(s);
            w.getShelves().get(2).addResource(s);
        } catch (Exception e) {
            fail();
        }

        assertTrue(w.getShelves().get(0).isEmpty());
        assertFalse(w.getShelves().get(0).isFull());

        assertFalse(w.getShelves().get(1).isEmpty());
        assertFalse(w.getShelves().get(1).isFull());

        assertFalse(w.getShelves().get(2).isEmpty());
        assertTrue(w.getShelves().get(2).isFull());

        assertNull(w.getShelves().get(0).getResType());
        assertEquals(0, w.getShelves().get(0).getQuantity());

        assertEquals(c, w.getShelves().get(1).getResType());
        assertEquals(1, w.getShelves().get(1).getQuantity());

        assertEquals(s, w.getShelves().get(2).getResType());
        assertEquals(3, w.getShelves().get(2).getQuantity());
    }
}
