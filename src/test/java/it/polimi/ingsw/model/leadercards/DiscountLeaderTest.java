package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Stream;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test of properties of DiscountLeader.
 */
public class DiscountLeaderTest {
    /**
     * Factory method for parameters combination.
     * 
     * @return  stream of arguments. The arguments are combinations of parameters.
     */
    private static Stream<Arguments> provideParameters() {
        List<Arguments> arguments = new ArrayList<>(); // arguments for each test call

        ResourceTypeFactory resTypeFactory = new JavaResourceTypeFactory();

        Map<ResourceType, Integer> ogZeroCost = new HashMap<>(),
                                   ogNonemptyCost = new HashMap<>();
        ogZeroCost.put(resTypeFactory.get("Coin"), 0); // test against cost set to zero
        ogNonemptyCost.put(resTypeFactory.get("Coin"), 1);

        List<Map<ResourceType, Integer>> ogCosts = Arrays.asList(null, ogZeroCost, ogNonemptyCost); // cost maps to discount
        List<Integer> discounts = Arrays.asList(-1, 0, 1); // discount amounts

        // build every possible combination of the above
        discounts.forEach(discount ->
            ogCosts.forEach(cost ->
                arguments.add(Arguments.of(resTypeFactory, discount, cost))));

        return arguments.stream();
    }

    /**
     * Tests whether the discount process works given a certain cost map.
     * 
     * @param resTypeFactory    the factory to produce resources.
     * @param discount          the discount's amount to be applied.
     * @param ogCost            the original cost to check the result against.
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    void getDevCardCost(ResourceTypeFactory resTypeFactory, int discount, Map<ResourceType, Integer> ogCost) {
        DiscountLeader leader = new DiscountLeader(discount, 0, null, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        Map<ResourceType, Integer> postCost = leader.getDevCardCost(ogCost);

        if (ogCost == null)
            assertNull(leader.getDevCardCost(ogCost));
        else
            ogCost.forEach((r, c) -> assertEquals(r != resTypeFactory.get("Coin") ? c : (c == null ? null : c - discount), postCost.get(r)));
    }
}
