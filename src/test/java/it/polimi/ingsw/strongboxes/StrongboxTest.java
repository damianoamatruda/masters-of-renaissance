package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.resourcetypes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Strongbox class.
 */
public class StrongboxTest {
    @Test
    public void testQuantity1() {
        Strongbox s = new Strongbox();
        ResourceType c = Coin.getInstance();

        assertTrue(s.isEmpty());
        assertEquals(0, s.getResourceQuantity(c));
    }

    @Test
    public void testQuantity2() {
        Strongbox s = new Strongbox();
        ResourceType c = Coin.getInstance();

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
        assertEquals(0, s.getResourceQuantity(c));
    }

    @Test
    public void testAddGet() {
        Strongbox s = new Strongbox();
        ResourceType c = Coin.getInstance();

        try {
            s.addResource(c);
            s.addResource(c);
            s.addResource(c);
        } catch (Exception e) {
            fail();
        }

        assertFalse(s.isEmpty());
        assertEquals(3, s.getResourceQuantity(c));
    }
}
