package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the functionalities of the class "ActionTokenBlackMoveOneShuffle".
 */
class ActionTokenBlackMoveOneShuffleTest {
    /**
     * Ensures Lorenzo's marker has advanced by 1 after triggering the "Advance by 1 and shuffle" token.
     */
    @Test
    @DisplayName("Move one and shuffle")
    void testBlackAdvancement() {
        ActionToken token = new ActionTokenBlackMoveOneShuffle();
        List<ActionToken> stack = new ArrayList<>();
        stack.add(token);
        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), stack, 0, 0);

        token.trigger(solo);

        assertEquals(1, solo.getBlackPoints());
    }
}