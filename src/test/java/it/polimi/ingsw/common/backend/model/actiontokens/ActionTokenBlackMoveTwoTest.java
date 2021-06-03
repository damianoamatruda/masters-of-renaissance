package it.polimi.ingsw.common.backend.model.actiontokens;

import it.polimi.ingsw.common.backend.model.*;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
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
        ActionToken token = new ActionTokenBlackMoveTwo(0);

        ResourceType r1 = new ResourceType("r1", true);

        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        SoloGame game = new SoloGame(player, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);

        token.trigger(game);

        assertEquals(game.getBlackPoints(), 2);
    }
}
