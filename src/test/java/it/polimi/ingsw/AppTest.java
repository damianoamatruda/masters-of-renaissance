package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for App.
 */
public class AppTest 
{
    /**
     * Example.
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    void test() throws FileNotFoundException, JAXBException {
        ModelConfig config = new FileGameFactory("src/main/resources/config.xml").unmarshall("src/main/resources/config.xml");
//        assertEquals(48, config.getDevCards().size());
//        assertEquals(3, config.getFaithTrack().getSections().size());
//        assertEquals(12, config.getFaithTrack().getSections().get(1).getBeginning());
//        assertNotEquals(config.getDevCards().get(0),config.getDevCards().get(1));
    }

    @Test
    void test2() throws JAXBException, FileNotFoundException {
        FileGameFactory f = new FileGameFactory("src/main/resources/config.xml");
//        assertEquals(48, f.generateDevCards().size());
//        assertNotEquals(f.generateDevCards().get(0).getLevel(), f.generateDevCards().get(40).getLevel());
        assertEquals(13, f.getModelConfig().getMarket().stream().mapToInt(m -> m.getAmount()).sum());
    }
}
