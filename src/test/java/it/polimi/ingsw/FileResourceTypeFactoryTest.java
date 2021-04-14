package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

public class FileResourceTypeFactoryTest {
    ModelConfig config;
    FileResourceTypeFactory factory;

    @BeforeEach
    void setup() throws FileNotFoundException, JAXBException {
        config = (ModelConfig) JAXBContext.newInstance(ModelConfig.class).createUnmarshaller()
                .unmarshal(new FileReader("src/main/resources/config.xml"));
        factory = new FileResourceTypeFactory(config);
    }

    @Test
    void generatedColors(){
        assertAll(()-> assertEquals(6, factory.generateResourceTypes().size()),
                ()-> assertEquals(1, factory.generateResourceTypes().stream().map(c -> c.getName()).filter(s -> s.equals("Coin")).count()),
                ()-> assertEquals(2, factory.generateResourceTypes().stream().filter(c -> !c.isStorable()).count()),
                ()-> assertEquals(0, factory.generateResourceTypes().stream().filter(s -> s.getName().equals("Faith")).filter(c -> c.isStorable()).count()));
    }
}