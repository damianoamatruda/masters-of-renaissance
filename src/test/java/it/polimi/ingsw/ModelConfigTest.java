package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ModelConfig.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModelConfigTest {
    ModelConfig config;

    @BeforeAll
    void setup() throws JAXBException, FileNotFoundException {
        config = (ModelConfig) JAXBContext.newInstance(ModelConfig.class).createUnmarshaller()
                .unmarshal(new FileReader("src/main/resources/config.xml"));
    }

    /**
     * Basic test of faith track parsing.
     */
    @Test
    void testConfigFaithTrack() {
        assertAll(() -> assertEquals(3, config.getFaithTrack().getSections().size()),
                () -> assertEquals(12, config.getFaithTrack().getSections().get(1).getBeginning()));
    }

    /**
     * Basic test of development cards parsing.
     */
    @Test
    void testConfigCards() {
        assertAll(() -> assertEquals(48, config.getDevCards().size()),
                () -> assertNotEquals(config.getDevCards().get(0), config.getDevCards().get(1)),
                () -> assertEquals(48, config.getDevCards().size()),
                () -> assertNotEquals(config.getDevCards().get(0).getLevel(), config.getDevCards().get(40).getLevel()));
    }

    /**
     * Basic test of market grid parsing.
     */
    @Test
    void testConfigMarket() {
        assertEquals(13, config.getMarket().stream().mapToInt(m -> m.getAmount()).sum());
    }

}
