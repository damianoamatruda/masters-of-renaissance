package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcetypes.*;
import it.polimi.ingsw.model.resourcecontainers.Shelf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Market class.
 */
public class MarketTest {
    /**
     * First test that checks that the right number of columns and rows in the market grid is gotten.
     */
    @Test
    public void checkColsRows1() {
        Market market = new Market(
                Map.of(Coin.getInstance(), 1,
                        Faith.getInstance(), 1,
                        Servant.getInstance(), 2,
                        Shield.getInstance(), 3),
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
                Map.of(Coin.getInstance(), 1,
                        Faith.getInstance(), 1,
                        Servant.getInstance(), 2,
                        Shield.getInstance(), 2,
                        Stone.getInstance(), 3,
                        Zero.getInstance(), 4),
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
                Map.of(Coin.getInstance(), 1,
                        Faith.getInstance(), 1,
                        Servant.getInstance(), 2,
                        Shield.getInstance(), 2,
                        Stone.getInstance(), 3,
                        Zero.getInstance(), 4),
                4);

        //assertEquals(4, market.getColsCount());
        //assertEquals(3, market.getRowsCount()); /* 12 / 4 */

        Game game = new Game(List.of("player"), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>());
        Player player = game.getPlayers().get(0);

        Map<ResourceType, Shelf> shelves = Map.of(
                Coin.getInstance(), new Shelf(12),
                Faith.getInstance(), new Shelf(12),     /* Ignored */
                Servant.getInstance(), new Shelf(12),
                Shield.getInstance(), new Shelf(12),
                Stone.getInstance(), new Shelf(12),
                Zero.getInstance(), new Shelf(12));     /* Ignored */

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

                Map<Shelf, Map<ResourceType, Integer>> outputShelves = output.entrySet().stream()
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
