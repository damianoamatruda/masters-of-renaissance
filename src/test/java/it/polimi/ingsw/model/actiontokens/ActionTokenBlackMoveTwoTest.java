package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveTwo".
 */
class ActionTokenBlackMoveTwoTest {
    /**
     * Ensures Lorenzo's marker has advanced by 2 after triggering the "Advance by 2" token.
     */
    @Test
    void trigger() {
        ActionToken token = new ActionTokenBlackMoveTwo();
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        SoloGame game = new SoloGame(player, new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), List.of(), 0, 0);

        token.trigger(game);

        assertEquals(game.getBlackPoints(), 2);
    }
}
