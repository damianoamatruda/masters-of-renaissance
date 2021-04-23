package it.polimi.ingsw.server.controller;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for FileGameFactory.
 */
public class FileGameFactoryTest {
    final FileGameFactory f = new FileGameFactory(getClass().getResourceAsStream("/config.json"));

    /**
     * Tests that the resource factory works properly.
     */
    @Test
    void testResources() {
        assertAll(
                () -> assertNotNull(f.getResourceType("Coin")),
                () -> assertNotNull(f.getResourceType("Faith")),
                () -> assertFalse(f.getResourceType("Faith").isStorable())
        );
    }

    /**
     * Tests that the color factory works properly.
     */
    @Test
    void testColors() {
        assertAll(
                () -> assertNotNull(f.getDevCardColor("Blue")),
                () -> assertNotNull(f.getDevCardColor("Green"))
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
        assertThrows(IllegalArgumentException.class, () -> f.getSoloGame(null));
    }
}
