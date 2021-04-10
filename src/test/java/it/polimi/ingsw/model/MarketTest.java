package it.polimi.ingsw.model;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.*;
import it.polimi.ingsw.model.resourcecontainers.Shelf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Market class.
 */
public class MarketTest {
    ResourceTypeFactory resTypeFactory;
    
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * First test that checks that the right number of columns and rows in the market grid is gotten.
     */
    @Test
    public void checkColsRows1() {
        Market market = new Market(
                Map.of(resTypeFactory.get("Coin"), 1,
                        resTypeFactory.get("Faith"), 1,
                        resTypeFactory.get("Servant"), 2,
                        resTypeFactory.get("Shield"), 3),
        3);

        assertEquals(3, market.getColsCount());
        assertEquals(2, market.getRowsCount()); /* 6 / 3 */
    }

    /**
     * Second test that checks that the right number of columns and rows in the market grid is gotten.
     */
    @Test
    public void checkColsRows2() {
        Market market = new Market(
                Map.of(resTypeFactory.get("Coin"), 1,
                        resTypeFactory.get("Faith"), 1,
                        resTypeFactory.get("Servant"), 2,
                        resTypeFactory.get("Shield"), 2,
                        resTypeFactory.get("Stone"), 3,
                        resTypeFactory.get("Zero"), 4),
                4);

        assertEquals(4, market.getColsCount());
        assertEquals(3, market.getRowsCount()); /* 12 / 4 */
    }

    /**
     * Checks that a row of the market grid remains the same after a complete shift.
     */
    @Test
    public void checkShiftRow() {
        Market market = new Market(
                Map.of(resTypeFactory.get("Coin"), 1,
                        resTypeFactory.get("Faith"), 1,
                        resTypeFactory.get("Servant"), 2,
                        resTypeFactory.get("Shield"), 2,
                        resTypeFactory.get("Stone"), 3,
                        resTypeFactory.get("Zero"), 4),
                4);

        //assertEquals(4, market.getColsCount());
        //assertEquals(3, market.getRowsCount()); /* 12 / 4 */

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        Map<ResourceType, Shelf> shelves = Map.of(
                resTypeFactory.get("Coin"), new Shelf(12),
                resTypeFactory.get("Faith"), new Shelf(12),     /* Ignored */
                resTypeFactory.get("Servant"), new Shelf(12),
                resTypeFactory.get("Shield"), new Shelf(12),
                resTypeFactory.get("Stone"), new Shelf(12),
                resTypeFactory.get("Zero"), new Shelf(12));     /* Ignored */

        for (int rowIndex = 0; rowIndex < market.getRowsCount(); rowIndex++) {
            List<ResourceType> rowBefore = new ArrayList<>(market.getGrid().get(rowIndex));

            for (int shiftNum = 0; shiftNum < market.getColsCount() + 1; shiftNum++) {
                Map<ResourceType, Integer> output = new HashMap<>();
                for (int i = 0; i < market.getColsCount(); i++) {
                    ResourceType resType = market.getGrid().get(rowIndex).get(i);
                    // TODO: Add method 'isMarketBlank' to ResourceType
                    if (!resType.getName().equals("Zero") && resType.isStorable())
                        output.merge(resType, 1, Integer::sum);
                }

                Map<ResourceContainer, Map<ResourceType, Integer>> outputShelves = output.entrySet().stream()
                        .collect(Collectors.toMap(e -> shelves.get(e.getKey()), e -> new HashMap<>(Map.of(e.getKey(), e.getValue()))));

                try {
                    market.takeResources(game, player, true, rowIndex, new HashMap<>(), outputShelves);
                } catch (Exception e) {
                    fail();
                }
            }

            List<ResourceType> rowAfter = new ArrayList<>(market.getGrid().get(rowIndex));
            if (!rowAfter.equals(rowBefore))
                fail();
        }
    }
}
