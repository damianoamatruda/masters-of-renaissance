package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ResourceShelf class.
 */
public class ResourceShelfTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Tests the quantity of resources of a type.
     */
    @Test
    public void testQuantity1() {
        ResourceType c = resTypeFactory.get("Coin");
        ResourceShelf s = new ResourceShelf(c, 3);

        assertTrue(s.isEmpty());
        assertFalse(s.isFull());
        assertNull(s.getResType());
        assertEquals(c, s.getBoundedResType());
        assertEquals(0, s.getQuantity());
        assertEquals(s.getResourceQuantity(s.getBoundedResType()), s.getQuantity());
    }

    /**
     * Tests the quantity of resources of a type.
     */
    @Test
    public void testQuantity2() {
        ResourceType c = resTypeFactory.get("Coin");
        ResourceShelf s = new ResourceShelf(c, 3);

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
        assertEquals(c, s.getBoundedResType());
        assertEquals(0, s.getQuantity());
        assertEquals(s.getResourceQuantity(s.getBoundedResType()), s.getQuantity());
    }

    /**
     * Tests by adding a resource multiple times and getting it.
     */
    @Test
    public void testAddGet() {
        ResourceType c = resTypeFactory.get("Coin");
        ResourceShelf s = new ResourceShelf(c, 3);

        try {
            for (int i = 0; i < 3; i++)
                s.addResource(c);
        } catch (Exception e) {
            fail();
        }

        assertFalse(s.isEmpty());
        assertTrue(s.isFull());
        assertEquals(c, s.getResType());
        assertEquals(c, s.getBoundedResType());
        assertEquals(3, s.getQuantity());
        assertEquals(s.getResourceQuantity(s.getBoundedResType()), s.getQuantity());
    }
}
