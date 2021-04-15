package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Development Card grid operations
 */

public class DevCardGridTest {
    DevCardGrid devCardGrid;
    Player buyer;
    Game game;
    ResourceType r1 = new ResourceType("r1", true);
    DevCardColor c1 = new DevCardColor("c1");
    DevCardColor c2 = new DevCardColor("c2");

    @BeforeEach
    void setup() {
        Set<DevCardColor> colors = Set.of(c1, c2);
        int levelsCount = 2;
        List<DevelopmentCard> devCards = new ArrayList<>();
        for (DevCardColor color : colors)
            for (int level = 1; level <= levelsCount; level++)
                for (int i = 1; i <= 2; i++)
                    devCards.add(new DevelopmentCard(
                            color, level,
                            new ResourceRequirement(new HashMap<>() {{
                                put(r1, 1);
                            }}),
                            new Production(Map.of(), 0, Map.of(), 0),
                            3 * i
                    ));
        devCardGrid = new DevCardGrid(devCards, levelsCount, colors.size());

        buyer = new Player(
                "",
                true, List.of(),
                new Warehouse(0), new Strongbox(),
                new Production(Map.of(), 0, Map.of(), 0), 1,
                0, 0);
        game = new Game(
                List.of(buyer),
                devCardGrid,
                null,
                new FaithTrack(Set.of(), Set.of()), 0,
                2
        );
    }


    /**
     * Checks correct instantiation of devGrid with original rules.
     *
     * @param level level of the cards to test
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void devGridTest(int level) {
        assertAll(() -> assertEquals(2, devCardGrid.getDeck(c1, level).size()),
                () -> assertEquals(2, devCardGrid.getDeck(c2, level).size()));
    }

    /**
     * Basic test on peek of what cards can be purchased during the current turn.
     */
    @Test
    void peekCardsTest() {
        List<List<DevelopmentCard>> top = devCardGrid.peekDevCards();
        for (List<DevelopmentCard> developmentCards : top) {
            DevCardColor color = developmentCards.get(0).getColor();
            for (int level = 1; level <= developmentCards.size(); level++)
                assertSame(devCardGrid.getDeck(color, level).peek(), developmentCards.get(level - 1));
        }
    }

    /**
     * Basic test on development card purchase.
     */
    @Test
    void buyCardTest() throws Exception {
        buyer.getStrongbox().addResource(r1);

        List<List<DevelopmentCard>> oldTop = devCardGrid.peekDevCards();

        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
            put(buyer.getStrongbox(), new HashMap<>() {{
                put(r1, 1);
            }});
        }};
        devCardGrid.buyDevCard(game, buyer, c1, 1, 0, resContainers);

        List<List<DevelopmentCard>> newTop = devCardGrid.peekDevCards();

        assertAll(() -> assertNotEquals(oldTop, newTop),
                () -> assertEquals(buyer.getDevSlots().get(0).peek().getColor(), c1),
                () -> assertEquals(buyer.getDevSlots().get(0).peek().getLevel(), 1));

    }

    /**
     * Checks that level 2+ cards cannot fit in an empty player card slot.
     */
    @Test
    void buyCardWrongLevel() {
        buyer.getStrongbox().addResource(r1);

        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
            put(buyer.getStrongbox(), new HashMap<>() {{
                put(r1, 1);
            }});
        }};

        assertThrows(IllegalCardDepositException.class, () -> devCardGrid.buyDevCard(game, buyer, c1, 2, 0, resContainers));
    }
}
