package it.polimi.ingsw.model.cardrequirements;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.polimi.ingsw.JavaDevCardColorFactory;
import it.polimi.ingsw.JavaGameFactory;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.devcardcolors.*;

/**
 * Test class for DevCardRequirement.
 */
public class DevCardRequirementTest {
    private DevCardColorFactory devCardColorFactory;
    
    @BeforeEach
    void setup() {
        devCardColorFactory = new JavaDevCardColorFactory();
    }
    
    /**
     * Tests the requirements' checking when the card's color is different from 
     */
    @Test
    void checkReqsWrongColor() {
        DevelopmentCard devCard = new DevelopmentCard(devCardColorFactory.get("Green"), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 1, 1)));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongLevel() {
        DevelopmentCard devCard = new DevelopmentCard(devCardColorFactory.get("Green"), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 2, 1)));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongAmount() {
        DevelopmentCard devCard = new DevelopmentCard(devCardColorFactory.get("Green"), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 1, 2)));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkRequirements() {
        DevelopmentCard devCard = new DevelopmentCard(devCardColorFactory.get("Green"), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 3);
        try {
            p.addToDevSlot(new JavaGameFactory().buildSoloGame("Marco"), 0, devCard, Map.of());
        } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 1, 1)));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
