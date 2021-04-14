package it.polimi.ingsw.model;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Development Card grid operations
 */

public class DevCardGridTest {
    GameFactory factory;
    Game game;
    @BeforeEach
    void setup(){
        factory = new FileGameFactory("src/main/resources/config.xml");
        game = factory.buildMultiGame(List.of("Alessandro", "Damiano", "Marco"));
    }


    /**
     * Checks correct instantiation of devGrid with original rules.
     *
     * @param level level of the cards to test
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void devGridTest(int level) {
        assertAll(() -> assertEquals(4, game.getDevCardGrid().getDeck(factory.getDevCardColor("Blue"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(factory.getDevCardColor("Green"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(factory.getDevCardColor("Purple"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(factory.getDevCardColor("Yellow"), level).size()));
    }

    /**
     * Basic test on peek of what cards can be purchased during the current turn.
     */
    @RepeatedTest(value = 10)
    void peekCardsTest() {
        Game otherGame = new FileGameFactory("src/main/resources/config.xml").buildMultiGame(List.of("Alessandro", "Damiano", "Marco"));
        DevCardGrid grid = otherGame.getDevCardGrid();
        List<List<DevelopmentCard>> top = grid.peekDevCards();
        for(int i = 0; i < top.size(); i++)
            for(int j = 1; j <= top.get(i).size(); j++){
                DevCardColor color = top.get(i).get(0).getColor();
                if(grid.getDeck(color,j).peek() != top.get(i).get(j-1)) fail();
            }
        assert true;
    }

    /**
     * Basic test on development card purchase.
     */
    @Test
    void buyCardTest() throws Exception {
        DevCardGrid theGrid = game.getDevCardGrid();
        Stack<DevelopmentCard> deck = theGrid.getDeck(factory.getDevCardColor("Blue"), 1);
        deck.push(new DevelopmentCard(factory.getDevCardColor("Blue"), 1, new ResourceRequirement(new HashMap<>(){{
            put(factory.getResType("Coin"), 1);
        }}), null, 0));
        Player buyer = game.getPlayers().get(0);
        List<List<DevelopmentCard>> oldGrid = game.getDevCardGrid().peekDevCards();
        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
            put(buyer.getStrongbox(), new HashMap<>() {{
                put(factory.getResType("Coin"), 1);
            }});
        }};

        buyer.getStrongbox().addResource(factory.getResType("Coin"));

        theGrid.buyDevCard(game, buyer, factory.getDevCardColor("Blue"), 1, 0, resContainers);
        List<List<DevelopmentCard>> top = theGrid.peekDevCards();

        assertAll(() -> assertNotEquals(oldGrid, top),
                () -> assertEquals(buyer.getDevSlots().get(0).peek().getColor(), factory.getDevCardColor("Blue")),
                () -> assertEquals(buyer.getDevSlots().get(0).peek().getLevel(), 1));

    }

    /**
     * Checks that level 2+ cards cannot fit in an empty player card slot.
     */
    @Test
    void buyCardWrongLevel(){
        DevCardGrid theGrid = game.getDevCardGrid();
        Player buyer = game.getPlayers().get(0);
        Stack<DevelopmentCard> deck = theGrid.getDeck(factory.getDevCardColor("Blue"), 1);

        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
            put(buyer.getStrongbox(), new HashMap<>() {{
                put(factory.getResType("Coin"), 1);
            }});
        }};

        deck.push(new DevelopmentCard(factory.getDevCardColor("Blue"), 1, new ResourceRequirement(new HashMap<>(){{
            put(factory.getResType("Coin"), 1);
        }}), null, 0));

        assertThrows(Exception.class, () -> theGrid.buyDevCard(game, buyer, factory.getDevCardColor("Blue"), 1, 0, resContainers));
    }
}
