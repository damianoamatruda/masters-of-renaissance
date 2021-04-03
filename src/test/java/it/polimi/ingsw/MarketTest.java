package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.*;
import it.polimi.ingsw.strongboxes.Shelf;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Market class.
 */
public class MarketTest {
    @Test
    public void checkColsRows1() {
        Market market = new Market(new HashMap<>(){{
            put(Coin.getInstance(), 1);
            put(Faith.getInstance(), 1);
            put(Servant.getInstance(), 2);
            put(Shield.getInstance(), 2);
            put(Stone.getInstance(), 3);
            put(Zero.getInstance(), 4);
        }}, 4);

        assertEquals(4, market.getColsCount());
        assertEquals(3, market.getRowsCount()); /* 12 / 4 */
    }

    @Test
    public void checkColsRows2() {
        Market market = new Market(new HashMap<>() {{
            put(Coin.getInstance(), 1);
            put(Faith.getInstance(), 1);
            put(Servant.getInstance(), 2);
            put(Shield.getInstance(), 3);
        }}, 3);

        assertEquals(3, market.getColsCount());
        assertEquals(2, market.getRowsCount()); /* 6 / 3 */
    }

    @Test
    public void checkShiftRow() {
        Market market = new Market(new HashMap<>() {{
            put(Coin.getInstance(), 1);
            put(Faith.getInstance(), 1);
            put(Servant.getInstance(), 2);
            put(Shield.getInstance(), 2);
            put(Stone.getInstance(), 3);
            put(Zero.getInstance(), 4);
        }}, 4);

        //assertEquals(4, market.getColsCount());
        //assertEquals(3, market.getRowsCount()); /* 12 / 4 */

        Game game = new Game(new ArrayList(){{
            add("player");
        }});
        Player player = game.getPlayers().get(0);
        Map<ResourceType, Shelf> shelves = new HashMap<>(){{
            put(Coin.getInstance(), new Shelf(12));
            put(Faith.getInstance(), new Shelf(12)); /* Ignored */
            put(Servant.getInstance(), new Shelf(12));
            put(Shield.getInstance(), new Shelf(12));
            put(Stone.getInstance(), new Shelf(12));
            put(Zero.getInstance(), new Shelf(12)); /* Ignored */
        }};

        for (int index = 0; index < market.getRowsCount(); index++) {
            final int indexf = index;
            List<ResourceType> rowBefore = new ArrayList<>(market.getGrid().get(index));
            for (int j = 0; j < market.getColsCount() + 1; j++) {
                Map<ResourceType, Integer> output = IntStream
                        .range(0, market.getColsCount())
                        .mapToObj(i -> market.getGrid().get(indexf).get(i))
                        .filter(resType -> !resType.isBlank() && resType.isStorable())
                        .collect(Collectors.toMap(resType -> resType, resType -> 1, Integer::sum));

                Map<Shelf, Map<ResourceType, Integer>> outputShelves = output.entrySet().stream()
                        .collect(Collectors.toMap(e -> shelves.get(e.getKey()), e -> new HashMap<ResourceType, Integer>(){{
                            put(e.getKey(), e.getValue());
                        }}));

                try {
                    market.takeResources(game, player, true, index, new HashMap<>(), outputShelves);
                } catch (Exception e) {
                    fail();
                }
            }
            List<ResourceType> rowAfter = new ArrayList<>(market.getGrid().get(index));
            if (!rowAfter.equals(rowBefore))
                fail();
        }
    }
}
