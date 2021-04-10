package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Strongbox class.
 */
public class StrongboxTest {
    private ResourceTypeFactory resTypeFactory;
    
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }

    /**
     * Tests the quantity of resource of a type.
     */
    @Test
    public void testQuantity1() {
        Strongbox s = new Strongbox();
        ResourceType c = resTypeFactory.get("Coin");

        assertTrue(s.isEmpty());
        assertEquals(0, s.getResourceQuantity(c));
        assertEquals(0, s.getQuantity());
    }

    /**
     * Tests the quantity of resources of a type.
     */
    @Test
    public void testQuantity2() {
        Strongbox s = new Strongbox();
        ResourceType c = resTypeFactory.get("Coin");

        try {
            for (int i = 0; i < 3; i++)
                s.addResource(c);
            for (int i = 0; i < 3; i++)
                s.removeResource(c);
        } catch (Exception e) {
            fail();
        }

        assertTrue(s.isEmpty());
        assertEquals(0, s.getResourceQuantity(c));
        assertEquals(0, s.getQuantity());
    }

    /**
     * Tests by adding a resource multiple times and getting it.
     */
    @Test
    public void testAddGet() {
        Strongbox s = new Strongbox();
        ResourceType c = resTypeFactory.get("Coin");

        try {
            for (int i = 0; i < 3; i++)
                s.addResource(c);
        } catch (Exception e) {
            fail();
        }

        assertFalse(s.isEmpty());
        assertEquals(3, s.getResourceQuantity(c));
        assertEquals(3, s.getQuantity());
    }
}
