package it.polimi.ingsw.leadercards;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.resourcetypes.*;

/**
 * Test of properties of DiscountLeader
 */
public class DiscountLeaderTest {
    /**
     * Factory method for parameters combination.
     * 
     * @return  stream of arguments.
     *          The arguments are combinations of parameters.
     */
    private static Stream<Arguments> provideParameters() {
        List<Arguments> arguments = new ArrayList<>();                  // arguments for each test call

        Map<ResourceType, Integer> ogZeroCost = new HashMap<>(),
                                   ogNonemptyCost = new HashMap<>();
        ogZeroCost.put(Coin.getInstance(), 0);                          // test against cost set to zero
        ogNonemptyCost.put(Coin.getInstance(), 1);

        List<Map<ResourceType, Integer>> ogCosts = Arrays.asList(null, ogZeroCost, ogNonemptyCost); // cost maps to discount
        List<Integer> discounts = Arrays.asList(-1, 0, 1);                      // discount amounts

        // build every possible combination of the above
        discounts.forEach(discount ->
            ogCosts.forEach(cost ->
                arguments.add(Arguments.of(discount, cost))));

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void getDevCardCost(int discount, Map<ResourceType, Integer> ogCost) {
        DiscountLeader leader = new DiscountLeader(discount, Coin.getInstance(), null, 0);
        Player p = new Player("", List.of(leader), false, 3, 3, 9);

        try { leader.activate(p); } catch (Exception e) { }
        
        Map<ResourceType, Integer> postCost = leader.getDevCardCost(ogCost);

        if (ogCost == null)
            assertNull(leader.getDevCardCost(ogCost));
        else
            ogCost.forEach((r, c) -> assertEquals(r != Coin.getInstance() ? c : (c == null ? null : c - discount), postCost.get(r)));
    }
}
