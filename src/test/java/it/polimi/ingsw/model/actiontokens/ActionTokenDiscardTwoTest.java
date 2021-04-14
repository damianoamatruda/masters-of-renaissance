package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.DevCardColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenDiscardTwo".
 */

class ActionTokenDiscardTwoTest {
    GameFactory factory;
    SoloGame game;
    Player player;

    @BeforeEach
    void setup(){
        factory = new FileGameFactory("src/main/resources/config.xml");
        game = factory.buildSoloGame("Alessandro");
        player = game.getPlayers().get(0);
    }

    /**
     * Checks token only affects one color, other deck of other colors are left unchanged.
     */
    @Test
    void triggerFirstTurn() {
        ActionToken token = new ActionTokenDiscardTwo(factory.getDevCardColor("Blue"));
        DevCardGrid grid = game.getDevCardGrid();
        boolean foundTheChange = false;
        List<List<DevelopmentCard>> top = grid.peekDevCards();
        token.trigger(game);

        for(int i = 0; i < top.size(); i++)
            for(int j = 1; j <= top.get(i).size(); j++){
                DevCardColor color = top.get(i).get(0).getColor();
                if(grid.getDeck(color,j).peek() != top.get(i).get(j-1) && color != factory.getDevCardColor("Blue")) fail();
                else if (!foundTheChange && grid.getDeck(color,j).peek() == top.get(i).get(j-1) && color == factory.getDevCardColor("Blue")) fail();
                else if (!foundTheChange && grid.getDeck(color,j).peek() != top.get(i).get(j-1) && color == factory.getDevCardColor("Blue")) foundTheChange = true;
            }
        assert true;
    }

    /**
     * Checks the token effect when level 1 deck only has 1 card, so that the token also discards one level 2 card.
     *
     * @param colorString   the color name to be tested
     */
    @ParameterizedTest
    @ValueSource(strings = {"Blue", "Yellow", "Purple", "Green"})
    void discardDifferentLevels(String colorString){
        DevCardColor color = factory.getDevCardColor(colorString);
        ActionToken token = new ActionTokenDiscardTwo(color);
        DevCardGrid grid = game.getDevCardGrid();
        int initialSize = grid.getDeck(color,2).size();

        if(grid.getDeck(color, 1).size() % 2 == 0) grid.getDeck(color, 1).pop();

        while(grid.getDeck(color, 1).size() > 1)
            token.trigger(game);
        token.trigger(game);
        assertAll(()->assertEquals(grid.getDeck(color, 1).size(),0),
                ()-> assertEquals(grid.getDeck(color,2).size(), initialSize - 1));
    }

    /**
     * Checks the token effect / the game state when all the cards of one color are no more available.
     * Last card is discarded by the token.
     *
     * @param colorString   the color name to be tested
     */
    @ParameterizedTest
    @ValueSource(strings = {"Blue", "Yellow", "Purple", "Green"})
    void discardEndOfGame(String colorString){
        DevCardColor color = factory.getDevCardColor(colorString);
        ActionToken token = new ActionTokenDiscardTwo(color);
        DevCardGrid grid = game.getDevCardGrid();
        int level;

        for (level = 1; level < factory.parseLevelsCount(); level++)
            while(grid.getDeck(color, level).size() >= 1)
                grid.getDeck(color, level).pop();

        while(grid.getDeck(color, level).size() > 1)
            grid.getDeck(color, level).pop();

        if(grid.getDeck(color, level).size() % 2 == 0) grid.getDeck(color, 1).pop();

        token.trigger(game);
        game.hasEnded();
        assertAll(()->assertTrue(game.hasEnded()),
                ()-> assertTrue(game.isBlackWinner()),
                ()->assertFalse(player.isWinner()));
    }
}
