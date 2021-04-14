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
    FileGameFactory f;
    ModelConfig config;

    @BeforeEach
    void setup() throws JAXBException, FileNotFoundException {
        f = new FileGameFactory("src/main/resources/config.xml");
        config = (ModelConfig) JAXBContext.newInstance(ModelConfig.class).createUnmarshaller()
                .unmarshal(new FileReader("src/main/resources/config.xml"));
    }

    @Test
    void testConfig() throws FileNotFoundException, JAXBException {
        assertAll(()-> assertEquals(48, config.getDevCards().size()),
                ()-> assertEquals(3, config.getFaithTrack().getSections().size()),
                ()-> assertEquals(12, config.getFaithTrack().getSections().get(1).getBeginning()),
                ()-> assertNotEquals(config.getDevCards().get(0),config.getDevCards().get(1)),
                ()->assertEquals(13, config.getMarket().stream().mapToInt(m -> m.getAmount()).sum()));
    }

    @Test
    void testCards() {
//        assertAll(()-> assertEquals(48, f.generateDevCards().size()),
//                ()->assertNotEquals(f.generateDevCards().get(0).getLevel(), f.generateDevCards().get(40).getLevel()));
    }

    @Test
    void testResources() {
        assertAll(
                () -> assertNotNull(f.getResType("Coin")),
                () -> {
                    assertNotNull(f.getResType("Faith"));
                    assertFalse(f.getResType("Faith").isStorable());
                }
        );
    }

    @Test
    void testColors() {
        assertNotNull(f.getDevCardColor("Blue"));
        assertNotNull(f.getDevCardColor("Green"));
    }
}
