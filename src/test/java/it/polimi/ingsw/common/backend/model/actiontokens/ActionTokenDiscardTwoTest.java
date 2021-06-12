package it.polimi.ingsw.common.backend.model.actiontokens;

import it.polimi.ingsw.common.backend.model.*;
import it.polimi.ingsw.common.backend.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenDiscardTwo".
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionTokenDiscardTwoTest {
    SoloGame game;
    Player player;
    final DevCardColor blue = new DevCardColor("Blue");
    final DevCardColor green = new DevCardColor("Green");
    final DevCardColor purple = new DevCardColor("Purple");
    final DevCardColor yellow = new DevCardColor("Yellow");

    @BeforeEach
    void setup() {
        Set<DevCardColor> colors = Set.of(blue, green, purple, yellow);
        int levelsCount = 3;
        List<DevelopmentCard> devCards = new ArrayList<>();
        for (DevCardColor color : colors)
            for (int level = 1; level <= levelsCount; level++)
                for (int i = 1; i <= 4; i++)
                    devCards.add(new DevelopmentCard(
                            color, level,
                            new ResourceRequirement(Map.of()),
                            new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0),
                            3 * i, 0
                    ));
        DevCardGrid devCardGrid = new DevCardGrid(devCards, levelsCount, colors.size());

        ResourceType r1 = new ResourceType("r1", true);

        player = new Player(
                "",
                true,
                List.of(),
                new Warehouse(0), new Strongbox(),
                new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 1,
                new PlayerSetup(0, 0, 0, Set.of()));
        game = new SoloGame(
                player,
                null, null, List.of(),
                devCards,
                List.of(),
                List.of(),
                List.of(new ActionToken() {
                    @Override
                    public void trigger(SoloGame game) {
                    }

                    @Override
                    public int getId() {
                        return 0;
                    }

                    @Override
                    public ReducedActionToken reduce() {
                        return null;
                    }
                }), devCardGrid,
                new Market(Map.of(r1, 1), 1, r1),
                new FaithTrack(Set.of(), Set.of(), 24), 3,
                2);
    }

    /**
     * Checks token only affects one color, other deck of other colors are left unchanged.
     */
    @Test
    void triggerFirstTurn() {
        ActionToken token = new ActionTokenDiscardTwo(0, blue);
        DevCardGrid grid = game.getDevCardGrid();
        boolean foundTheChange = false;
        List<List<DevelopmentCard>> top = grid.peekDevCards();
        token.trigger(game);

        for (List<DevelopmentCard> developmentCards : top)
            for (int j = 1; j <= developmentCards.size(); j++) {
                DevCardColor color = developmentCards.get(0).getColor();
                if (grid.getDeck(color, j).peek() != developmentCards.get(j - 1) && color != blue) fail();
                else if (!foundTheChange && grid.getDeck(color, j).peek() == developmentCards.get(j - 1) && color == blue)
                    fail();
                else if (!foundTheChange && grid.getDeck(color, j).peek() != developmentCards.get(j - 1) && color == blue)
                    foundTheChange = true;
            }
        assert true;
    }

    private Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(blue),
                Arguments.of(green),
                Arguments.of(purple),
                Arguments.of(yellow)
        );
    }

    /**
     * Checks the token effect when level 1 deck only has 1 card, so that the token also discards one level 2 card.
     *
     * @param color the color to be tested
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    void discardDifferentLevels(DevCardColor color) {
        ActionToken token = new ActionTokenDiscardTwo(0, color);
        DevCardGrid grid = game.getDevCardGrid();
        int initialSize = grid.getDeck(color, 2).size();

        if (grid.getDeck(color, 1).size() % 2 == 0) grid.getDeck(color, 1).pop();

        while (grid.getDeck(color, 1).size() > 1)
            token.trigger(game);
        token.trigger(game);
        assertAll(() -> assertEquals(grid.getDeck(color, 1).size(), 0),
                () -> assertEquals(grid.getDeck(color, 2).size(), initialSize - 1));
    }

    /**
     * Checks the token effect / the game state when all the cards of one color are no more available. Last card is
     * discarded by the token.
     *
     * @param color the color to be tested
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    void discardEndOfGame(DevCardColor color) {
        ActionToken token = new ActionTokenDiscardTwo(0, color);
        DevCardGrid grid = game.getDevCardGrid();
        int level;

        for (level = 1; level < grid.getLevelsCount(); level++)
            while (grid.getDeck(color, level).size() >= 1)
                grid.getDeck(color, level).pop();

        while (grid.getDeck(color, level).size() > 1)
            grid.getDeck(color, level).pop();

        if (grid.getDeck(color, level).size() % 2 == 0) grid.getDeck(color, 1).pop();

        token.trigger(game);
        game.onTurnEnd();
        assertAll(() -> assertTrue(game.hasEnded()),
                () -> assertTrue(game.isBlackWinner()),
                () -> assertFalse(player.isWinner()));
    }
}
