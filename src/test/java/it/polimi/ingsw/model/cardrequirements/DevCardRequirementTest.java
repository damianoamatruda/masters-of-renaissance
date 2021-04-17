package it.polimi.ingsw.model.cardrequirements;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for DevCardRequirement.
 */
public class DevCardRequirementTest {
    private Player p;
    private Game g;
    private final DevCardColor green = new DevCardColor("Green");
    private final DevCardColor blue = new DevCardColor("Blue");

    @BeforeEach
    void setup() {
        ResourceType r1 = new ResourceType("r1", true);
        p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 1, 0, 0);
        g = new Game(List.of(p), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of()), 0, 10);
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the color of the
     * cards the player possesses doesn't correspond to the requirement's.
     */
    @Test
    void checkReqsWrongColor() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(blue, 1, 1)));

        assertThrows(RequirementsNotMetException.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the level of the
     * cards the player possesses doesn't correspond to the requirement's.
     */
    @Test
    void checkReqsWrongLevel() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 2, 1)));

        assertThrows(RequirementsNotMetException.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the amount of
     * cards the player possesses being different from the requirement's.
     */
    @Test
    void checkReqsWrongAmount() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 1, 2)));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should pass.
     */
    @Test
    void checkRequirementsPass() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 1, 1)));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
