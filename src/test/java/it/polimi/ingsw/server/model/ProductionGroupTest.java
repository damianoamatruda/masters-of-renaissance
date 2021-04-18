package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for ProductionGroup.
 */
public class ProductionGroupTest {
    @Test
    void singleProductionWithoutBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of()), 0, 0);
        Production prod = new Production(
                Map.of(r1, 2),
                0,
                Set.of(),
                Map.of(r2, 3),
                0,
                Set.of(),
                false
        );

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        ProductionGroup.ProductionRequest pr1 = new ProductionGroup.ProductionRequest(
                prod,
                Map.of(),
                Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3))
        );
        ProductionGroup productionGroup = new ProductionGroup(List.of(pr1));
        productionGroup.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2))
        );
    }

    @Test
    void singleProductionWithInputBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of()), 0, 0);
        Production prod = new Production(Map.of(r1, 2), 3, Set.of(), Map.of(r2, 3), 0, Set.of(), false);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);
        for (int i = 0; i < 2; i++)
            player.getStrongbox().addResource(r3);

        ProductionGroup.ProductionRequest productionRequest = new ProductionGroup.ProductionRequest(
                prod,
                Map.of(r1, 1, r3, 2),
                Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 3, r3, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3))
        );
        ProductionGroup productionGroup = new ProductionGroup(List.of(productionRequest));
        productionGroup.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(0, player.getStrongbox().getResourceQuantity(r3)));
    }

    @Test
    void singleProductionWithOutputBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of()), 0, 0);
        Production prod = new Production(Map.of(r1, 2), 0, Set.of(), Map.of(r2, 3), 3, Set.of(), false);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        ProductionGroup.ProductionRequest productionRequest = new ProductionGroup.ProductionRequest(
                prod,
                Map.of(),
                Map.of(r2, 2, r3, 1),
                Map.of(player.getStrongbox(), Map.of(r1, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 5, r3, 1))
        );
        ProductionGroup productionGroup = new ProductionGroup(List.of(productionRequest));
        productionGroup.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(5, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(1, player.getStrongbox().getResourceQuantity(r3)));
    }
}
