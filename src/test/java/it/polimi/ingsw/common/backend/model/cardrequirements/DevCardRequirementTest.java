package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.*;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
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
    private final DevCardColor green = new DevCardColor("Green", "");
    private final DevCardColor blue = new DevCardColor("Blue", "");
    private Player p;
    private Game g;

    @BeforeEach
    void setup() {
        ResourceType r1 = new ResourceType("r1", "", true);
        p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 2, new PlayerSetup(0, 0, 0, Set.of()));
        g = new Game(List.of(p), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of(), 24), 3, 10);
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the color of the
     * cards the player possesses doesn't correspond to the requirement's.
     */
    @Test
    void checkReqsWrongColor() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1, 0);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(blue, 1, 1)));

        assertThrows(CardRequirementsNotMetException.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the level of the
     * cards the player possesses doesn't correspond to the requirement's.
     */
    @Test
    void checkReqsWrongLevel() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1, 0);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 2, 1)));

        assertThrows(CardRequirementsNotMetException.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should fail due to the quantity of
     * cards the player possesses being different from the requirement's.
     */
    @Test
    void checkReqsWrongQuantity() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1, 0);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 1, 2)));

        assertThrows(Exception.class, () -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where it should pass.
     */
    @Test
    void checkRequirementsPass() {
        DevelopmentCard devCard = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1, 0);
        DevelopmentCard devCard2 = new DevelopmentCard(green, 1, new ResourceRequirement(Map.of()), null, 1, 0);

        assertDoesNotThrow(() -> p.addToDevSlot(g, 0, devCard, Map.of()));
        assertDoesNotThrow(() -> p.addToDevSlot(g, 1, devCard2, Map.of()));

        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 1, 2)));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }

    /**
     * Tests whether the requirement checking process works in a scenario where the requirements are empty.
     */
    @Test
    void checkRequirementsEmpty() {
        DevCardRequirement req = new DevCardRequirement(Set.of(new DevCardRequirement.Entry(green, 1, 0)));

        assertDoesNotThrow(() -> req.checkRequirements(p));
    }
}
