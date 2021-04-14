package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class FileDevCardColorTest {
    ModelConfig config;
    FileDevCardColorFactory factory;

    @BeforeEach
    void setup() throws FileNotFoundException, JAXBException {
        config = (ModelConfig) JAXBContext.newInstance(ModelConfig.class).createUnmarshaller()
                .unmarshal(new FileReader("src/main/resources/config.xml"));
        factory = new FileDevCardColorFactory(config);
    }

    @Test
    void generatedColors(){
        assertAll(()-> assertEquals(4, factory.generateDevCardColors().size()),
                ()-> assertEquals(1, factory.generateDevCardColors().stream().map(c -> c.getName()).filter(s -> s.equals("Blue")).count()));
    }
}
