package it.polimi.ingsw.server.model.leadercards;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of properties of DiscountLeader.
 */
public class DiscountLeaderTest {
    private static final ResourceType coin = new ResourceType("Coin", true);

    /**
     * Factory method for parameters combination.
     *
     * @return stream of arguments. The arguments are combinations of parameters.
     */
    private static Stream<Arguments> provideParameters() {
        List<Arguments> arguments = new ArrayList<>(); // arguments for each test call

        Map<ResourceType, Integer> ogZeroCost = Map.of(coin, 0); // test against cost set to zero
        Map<ResourceType, Integer> ogNonemptyCost = Map.of(coin, 1);

        List<Map<ResourceType, Integer>> ogCosts = Arrays.asList(null, ogZeroCost, ogNonemptyCost); // cost maps to discount
        List<Integer> discounts = Arrays.asList(-1, 0, 1); // discount amounts

        // build every possible combination of the above
        discounts.forEach(discount ->
                ogCosts.forEach(cost ->
                        arguments.add(Arguments.of(discount, cost))));

        return arguments.stream();
    }

    /**
     * Tests whether the discount process works given a certain cost map.
     *
     * @param discount the discount's amount to be applied.
     * @param ogCost   the original cost to check the result against.
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    void getDevCardCost(int discount, Map<ResourceType, Integer> ogCost) {
        DiscountLeader leader = new DiscountLeader(discount, coin, null, 0);
        Player p = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0);

        assertDoesNotThrow(() -> leader.activate(p));

        Map<ResourceType, Integer> postCost = leader.getDevCardCost(ogCost);

        if (ogCost == null)
            assertNull(leader.getDevCardCost(null)); // leader.getDevCardCost(ogCost)
        else
            ogCost.forEach((r, c) -> assertEquals(r != coin ? c : (c == null ? null : c - discount), postCost.get(r)));
    }
}