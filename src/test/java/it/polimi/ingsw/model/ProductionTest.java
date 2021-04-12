package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Production.
 */
public class ProductionTest {
    @Test
    void productionWithoutBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Production production = new Production(Map.of(r1, 2), 0, Set.of(), Map.of(r2, 3), 0, Set.of(), false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        new ProductionGroup(List.of(
                new ProductionGroup.ProductionRequest(production, Map.of(), Map.of(),
                        Map.of(player.getStrongbox(), Map.of(r1, 2)),
                        Map.of(player.getStrongbox(), Map.of(r2, 3)))
        )).activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2))
        );
    }

    @Test
    void productionWithInputBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Production production = new Production(Map.of(r1, 2), 3, Set.of(), Map.of(r2, 3), 0, Set.of(), false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);
        for (int i = 0; i < 2; i++)
            player.getStrongbox().addResource(r3);

        new ProductionGroup(List.of(
                new ProductionGroup.ProductionRequest(production, Map.of(r1, 1, r3, 2), Map.of(),
                        Map.of(player.getStrongbox(), Map.of(r1, 3, r3, 2)),
                        Map.of(player.getStrongbox(), Map.of(r2, 3)))
        )).activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(0, player.getStrongbox().getResourceQuantity(r3)));
    }

    @Test
    void productionWithOutputBlanks() throws IllegalProductionActivationException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Production production = new Production(Map.of(r1, 2), 0, Set.of(), Map.of(r2, 3), 3, Set.of(), false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        new ProductionGroup(List.of(
                new ProductionGroup.ProductionRequest(production, Map.of(), Map.of(r2, 2, r3, 1),
                        Map.of(player.getStrongbox(), Map.of(r1, 2)),
                        Map.of(player.getStrongbox(), Map.of(r2, 5, r3, 1)))
        )).activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(5, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(1, player.getStrongbox().getResourceQuantity(r3)));
    }
}
