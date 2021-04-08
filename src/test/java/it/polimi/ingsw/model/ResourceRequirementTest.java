package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.ResourceRequirement;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for ResourceRequirement
 */
public class ResourceRequirementTest {
    @Test
    void checkReqsWrongRes() {
        Player p = new Player("", List.of(), false, 0, 0, 0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(Shield.getInstance(), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    @Test
    void checkRequirements() {
        Player p = new Player("", List.of(), false, 0, 0, 0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(Coin.getInstance(), 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
