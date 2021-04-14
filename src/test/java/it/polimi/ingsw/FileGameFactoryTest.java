package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
                () -> assertNotNull(f.getResType("Coin")),
                () -> assertNotNull(f.getResType("Faith")),
                () ->assertFalse(f.getResType("Faith").isStorable())
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
        assertAll(()-> assertThrows(IllegalArgumentException.class, ()-> f.buildMultiGame(List.of())),
                ()-> assertNotNull(f.buildMultiGame(List.of(""))));
    }

    /**
     * Tests building solo games.
     */
    @Test
    void testBuildSoloGame(){
        assertAll(()-> assertThrows(IllegalArgumentException.class, ()-> f.buildSoloGame(null)),
                ()-> assertNotNull(f.buildSoloGame("")));
    }
}
