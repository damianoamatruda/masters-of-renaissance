package it.polimi.ingsw.model;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.JavaDevCardColorFactory;
import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DevCardGridTest {
    FileGameFactory factory;
    Game game;
    DevCardColorFactory colorFactory;
    ResourceTypeFactory resTypeFactory;
    @BeforeAll
    void setup(){
        factory = new FileGameFactory("src/main/resources/config.xml");
        game = factory.buildMultiGame(List.of("Alessandro", "Damiano", "Marco"));
        colorFactory = factory.getDevCardColorFactory();
        resTypeFactory = factory.getResTypeFactory();
    }


    /**
     * Checks correct instantiation of devGrid with original rules.
     *
     * @param level level of the cards to test
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void devGridTest(int level) {
        assertAll(() -> assertEquals(4, game.getDevCardGrid().getDeck(colorFactory.get("Blue"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(colorFactory.get("Green"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(colorFactory.get("Purple"), level).size()),
                () -> assertEquals(4, game.getDevCardGrid().getDeck(colorFactory.get("Yellow"), level).size()));
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
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void buyCardTest(int level) {
        DevCardGrid theGrid = game.getDevCardGrid();
        Stack<DevelopmentCard> deck = theGrid.getDeck(colorFactory.get("Blue"), level);
        deck.push(new DevelopmentCard(colorFactory.get("Blue"), level, new ResourceRequirement(new HashMap<>(){{
            put(resTypeFactory.get("Coin"), 1);
        }}), null, 0));
        Player buyer = game.getPlayers().get(0);
        List<List<DevelopmentCard>> oldGrid = game.getDevCardGrid().peekDevCards();
        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>() {{
            put(buyer.getStrongbox(), new HashMap<>() {{
                put(resTypeFactory.get("Coin"), 1);
            }});
        }};
        try {
            resTypeFactory.get("Coin").addIntoContainer(buyer.getStrongbox());

            theGrid.buyDevCard(game, buyer, colorFactory.get("Blue"), level, 0, resContainers);
            List<List<DevelopmentCard>> top = theGrid.peekDevCards();

            assertAll(() -> assertNotEquals(oldGrid, top),
                    () -> assertEquals(buyer.getDevSlots().get(0).peek().getColor(), colorFactory.get("Blue")),
                    () -> assertEquals(buyer.getDevSlots().get(0).peek().getLevel(), level));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception has been thrown");
        }

    }
}
