package it.polimi.ingsw.model.cardrequirements;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Map;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for ResourceRequirement.
 */
public class ResourceRequirementTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    // TODO: Add Javadoc
    @Test
    void checkReqsWrongRes() {
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.getStrongbox().addResource(resTypeFactory.get("Coin")); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(resTypeFactory.get("Shield"), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkRequirements() {
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.getStrongbox().addResource(resTypeFactory.get("Coin")); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}