package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for ResourceRequirement.
 */
public class ResourceRequirementTest {
    // TODO: Add Javadoc
    @Test
    void checkReqsWrongRes() {
        Player p = new Player("", false, new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0, 0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(Shield.getInstance(), 1));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    // TODO: Add Javadoc
    @Test
    void checkRequirements() {
        Player p = new Player("", false, new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0, 0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }

        ResourceRequirement req = new ResourceRequirement(Map.of(Coin.getInstance(), 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
