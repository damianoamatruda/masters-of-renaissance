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
    public void productionWithoutBlanks() throws Exception {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Production production = new Production(Map.of(r1, 2), 0, Map.of(r2, 3), 0, false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        production.activate(game, player, Map.of(), Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3)));

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2))
        );
    }

    @Test
    public void productionWithInputBlanks() throws Exception {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Production production = new Production(Map.of(r1, 2), 3, Map.of(r2, 3), 0, false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);
        for (int i = 0; i < 2; i++)
            player.getStrongbox().addResource(r3);

        production.activate(game, player, Map.of(r1, 1, r3, 2), Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 3, r3, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3)));

        assertAll("getResourceQuantity",
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(0, player.getStrongbox().getResourceQuantity(r3)));
    }

    @Test
    public void productionWithOutputBlanks() throws Exception {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Production production = new Production(Map.of(r1, 2), 0, Map.of(r2, 3), 3, false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        for (int i = 0; i < 6; i++)
            player.getStrongbox().addResource(r1);

        production.activate(game, player, Map.of(), Map.of(r2, 2, r3, 1),
                Map.of(player.getStrongbox(), Map.of(r1, 2 )),
                Map.of(player.getStrongbox(), Map.of(r2, 5, r3, 1)));

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(5, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(1, player.getStrongbox().getResourceQuantity(r3)));
    }
}
