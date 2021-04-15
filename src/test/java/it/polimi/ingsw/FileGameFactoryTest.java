package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for FileGameFactory.
 */
public class FileGameFactoryTest {
    FileGameFactory f = new FileGameFactory("src/main/resources/config.xml");
    ModelConfig config;

    /**
     * Tests that the resource factory works properly.
     */
    @Test
    void testResources() {
        assertAll(
                () -> assertNotNull(f.getResourceType("Coin")),
                () -> assertNotNull(f.getResourceType("Faith")),
                () ->assertFalse(f.getResourceType("Faith").isStorable())
        );
    }

    /**
     * Tests that the color factory works properly.
     */
    @Test
    void testColors() {
        assertAll(
                ()-> assertNotNull(f.getDevCardColor("Blue")),
                ()-> assertNotNull(f.getDevCardColor("Green"))
        );
    }

    /**
     * Tests building multiplayer games.
     */
    @Test
    void testBuildGame(){
        assertAll(()-> assertThrows(IllegalArgumentException.class, ()-> f.getMultiGame(List.of())),
                ()-> assertNotNull(f.getMultiGame(List.of(""))));
    }

    /**
     * Tests building solo games.
     */
    @Test
    void testBuildSoloGame(){
        assertAll(()-> assertThrows(IllegalArgumentException.class, ()-> f.getSoloGame(null)),
                ()-> assertNotNull(f.getSoloGame("")));
    }
}
