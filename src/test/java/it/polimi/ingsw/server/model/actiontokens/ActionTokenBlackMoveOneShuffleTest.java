package it.polimi.ingsw.server.model.actiontokens;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        List<ActionToken> deck = new ArrayList<>();
        deck.add(token);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0, 0, Set.of());
        SoloGame solo = new SoloGame(player, List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), deck, 0, 0);

        token.trigger(solo);

        assertEquals(1, solo.getBlackPoints());
    }
}