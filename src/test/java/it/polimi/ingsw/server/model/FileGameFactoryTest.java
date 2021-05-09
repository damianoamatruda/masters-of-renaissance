package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for FileGameFactory.
 */
public class FileGameFactoryTest {
    final FileGameFactory f = new FileGameFactory(getClass().getResourceAsStream("/config/config.json"));

    /**
     * Tests that the resource factory works properly.
     */
    @Test
    void testResources() {
        assertAll(
                () -> assertNotNull(f.getResourceType("Coin")),
                () -> assertNotNull(f.getResourceType("Faith")),
                () -> assertTrue(f.getResourceType("Faith").isPresent() && !f.getResourceType("Faith").get().isStorable())
        );
    }

    /**
     * Tests that the color factory works properly.
     */
    @Test
    void testColors() {
        assertAll(
                () -> assertTrue(f.getDevCardColor("Blue").isPresent()),
                () -> assertTrue(f.getDevCardColor("Green").isPresent())
        );
    }

    /**
     * Tests building multiplayer games.
     */
    @Test
    void testGetMultiGame() {
        assertDoesNotThrow(() -> f.getMultiGame(List.of("a", "b", "c", "d")));
        assertThrows(IllegalArgumentException.class, () -> f.getMultiGame(List.of()));
    }

    /**
     * Tests building solo games.
     */
    @Test
    void testGetSoloGame() {
        assertDoesNotThrow(() -> f.getSoloGame(""));
    }
}
