package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.FileGameFactory;
import it.polimi.ingsw.JavaDevCardColorFactory;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test of the functionalities of the class "ActionTokenDiscardTwo".
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionTokenDiscardTwoTest {
    GameFactory factory;
    DevCardColorFactory colorFactory;
    ResourceTypeFactory resTypeFactory;
    SoloGame game;

    @BeforeAll
    void setup(){
        factory = new FileGameFactory("src/main/resources/config.xml");
        game = factory.buildSoloGame("Alessandro");
        colorFactory = factory.getDevCardColorFactory();
        resTypeFactory = factory.getResTypeFactory();
    }

    /**
     * Checks token only affects one color, other deck of other colors are left unchanged.
     */
    @Test
    void trigger() {
        ActionToken token = new ActionTokenDiscardTwo(colorFactory.get("Blue"));
        DevCardGrid grid = game.getDevCardGrid();
        boolean foundTheChange = false;
        List<List<DevelopmentCard>> top = grid.peekDevCards();
        token.trigger(game);

        for(int i = 0; i < top.size(); i++)
            for(int j = 1; j <= top.get(i).size(); j++){
                DevCardColor color = top.get(i).get(0).getColor();
                if(grid.getDeck(color,j).peek() != top.get(i).get(j-1) && color != colorFactory.get("Blue")) fail();
                else if (!foundTheChange && grid.getDeck(color,j).peek() == top.get(i).get(j-1) && color == colorFactory.get("Blue")) fail();
                else if (!foundTheChange && grid.getDeck(color,j).peek() != top.get(i).get(j-1) && color == colorFactory.get("Blue")) foundTheChange = true;
            }
        assert true;
    }
}
