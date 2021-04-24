package it.polimi.ingsw.server.model.cardrequirements;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for ResourceRequirement.
 */
public class ResourceRequirementTest {
    private Player p;
    private final ResourceType coin = new ResourceType("Coin", true);
    private final ResourceType shield = new ResourceType("Shield", true);

    @BeforeEach
    void setup() {
        p = new Player("", false, List.of(), new Warehouse(1), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 1, 0, 0, 0);
    }

    /**
     * Tests whether the checking process for a resource requirement fails if the player's resources don't satisfy it.
     */
    @Test
    void checkReqsWrongRes() {
        p.getStrongbox().addResource(coin);
        assertDoesNotThrow(() -> p.getWarehouse().getShelves().get(0).addResource(coin));

        ResourceRequirement req = new ResourceRequirement(Map.of(shield, 1));

        assertThrows(RequirementsNotMetException.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the checking process for a resource requirement succeeds if the player's resources satisfy it.
     */
    @Test
    void checkRequirements() {
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0);
        p.getStrongbox().addResource(coin);

        ResourceRequirement req = new ResourceRequirement(Map.of(coin, 1));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
