package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.devcardcolors.*;

/**
 * Test class for DevCardRequirement.
 */
public class DevCardRequirementTest {
    // TODO: Add Javadoc
    @Test
    void checkReqsWrongColor() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Blue.getInstance(), 1), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongLevel() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 2), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongAmount() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 1), 2));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkRequirements() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 3);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 1), 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
