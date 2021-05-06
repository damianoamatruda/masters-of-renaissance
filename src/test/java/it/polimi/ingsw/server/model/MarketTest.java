package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Market.
 */
public class MarketTest {
    @Test
    void getColsCount1() {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        ResourceType r4 = new ResourceType("r4", true);
        ResourceType r5 = new ResourceType("r5", true);
        Market market = new Market(Map.of(r1, 1, r2, 1, r3, 2, r4, 3), 3, r5);
        assertEquals(3, market.getColsCount());
    }

    @Test
    void getRowsCount1() {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        ResourceType r4 = new ResourceType("r4", true);
        ResourceType r5 = new ResourceType("r5", true);
        Market market = new Market(Map.of(r1, 1, r2, 1, r3, 2, r4, 3), 3, r5);
        assertEquals(2, market.getRowsCount()); /* 6 / 3 */
    }

    @Test
    void getColsCount2() {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        ResourceType r4 = new ResourceType("r4", true);
        ResourceType r5 = new ResourceType("r5", true);
        ResourceType r6 = new ResourceType("r6", true);
        Market market = new Market(Map.of(r1, 1, r2, 1, r3, 2, r4, 2, r5, 3, r6, 4), 4, r6);
        assertEquals(4, market.getColsCount());
    }

    @Test
    void getRowsCount2() {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        ResourceType r4 = new ResourceType("r4", true);
        ResourceType r5 = new ResourceType("r5", true);
        ResourceType r6 = new ResourceType("r6", true);
        Market market = new Market(Map.of(r1, 1, r2, 1, r3, 2, r4, 2, r5, 3, r6, 4), 4, r6);
        assertEquals(3, market.getRowsCount()); /* 12 / 4 */
    }

    /**
     * Checks that a row of the market grid remains the same after a complete shift.
     */
    @Test
    void completelyShiftRow() throws IllegalMarketTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", false);
        ResourceType r3 = new ResourceType("r3", true);
        ResourceType r4 = new ResourceType("r4", true);
        ResourceType r5 = new ResourceType("r5", true);
        ResourceType r6 = new ResourceType("r6", false);
        Market market = new Market(
                Map.of(r1, 1, r2, 1, r3, 2, r4, 2, r5, 3, r6, 4), 4, r6);

        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0, Set.of());
        Game game = new Game(List.of(player), List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of()), 0, 0);

        Map<ResourceType, Shelf> shelves = Map.of(
                r1, new Shelf(48),     /* market.getRowsCount() * (market.getColsCount() + 1) * market.getColsCount() */
                r2, new Shelf(48),     /* Ignored */
                r3, new Shelf(48),
                r4, new Shelf(48),
                r5, new Shelf(48),
                r6, new Shelf(48));    /* Ignored */

        for (int rowIndex = 0; rowIndex < market.getRowsCount(); rowIndex++) {
            List<ResourceType> rowBefore = new ArrayList<>(market.getGrid().get(rowIndex));

            for (int shiftNum = 0; shiftNum < market.getColsCount() + 1; shiftNum++) {
                Map<ResourceType, Integer> output = new HashMap<>();

                for (int i = 0; i < market.getColsCount(); i++) {
                    ResourceType resType = market.getGrid().get(rowIndex).get(i);
                    if (resType.isStorable())
                        output.merge(resType, 1, Integer::sum);
                }

                Map<Shelf, Map<ResourceType, Integer>> outputShelves = output.entrySet().stream()
                        .collect(Collectors.toMap(e -> shelves.get(e.getKey()), e -> Map.of(e.getKey(), e.getValue())));

                market.takeResources(game, player, true, rowIndex, Map.of(), outputShelves);
            }

            List<ResourceType> rowAfter = new ArrayList<>(market.getGrid().get(rowIndex));
            assertEquals(rowBefore, rowAfter);
        }
    }
}
