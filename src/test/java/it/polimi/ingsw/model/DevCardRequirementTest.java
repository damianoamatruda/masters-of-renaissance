package it.polimi.ingsw.model;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.polimi.ingsw.model.DevCardRequirement;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.ResourceRequirement;
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

        Player p = new Player("", null, false, 0, 0, 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Blue.getInstance(), 1), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongLevel() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", null, false, 0, 0, 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 2), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkReqsWrongAmount() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", null, false, 0, 0, 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 1), 2));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkRequirements() {
        DevelopmentCard devCard = new DevelopmentCard(Green.getInstance(), 1, new ResourceRequirement(Map.of()), null, 1);

        Player p = new Player("", null, false, 0, 1, 0);
        try { p.addToDevSlot(null, 0, devCard, Map.of()); } catch (Exception e) { }

        DevCardRequirement req = new DevCardRequirement(Map.of(Map.of(Green.getInstance(), 1), 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}