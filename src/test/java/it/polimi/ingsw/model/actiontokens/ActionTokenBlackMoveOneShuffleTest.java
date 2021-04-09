package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.GameFactory;
import it.polimi.ingsw.model.DevCardGrid;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.SoloGame;
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
    // TODO: Add Javadoc
    @Test
    @DisplayName("Move one and shuffle")
    void testBlackAdvancement() {
        ActionToken token = new ActionTokenBlackMoveOneShuffle();
        List<ActionToken> stack = new ArrayList<>();
        stack.add(token);
        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), stack, 0, 0);

        token.trigger(solo);

        assertEquals(1, solo.getBlackPoints());
    }

    // TODO: Add Javadoc
    @Test
    void testShuffle() {
        List<ActionToken> stack = new ArrayList<>();
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        stack.add(new ActionTokenBlackMoveOneShuffle());
        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production<>(Map.of(), 0, Map.of(), 0), 0);
        SoloGame solo = new SoloGame(player, new DevCardGrid(new ArrayList<>(), 0, 0), null, new GameFactory().generateVaticanSections(), new GameFactory().generateYellowTiles(), stack, 0, 0);
        List stack2 = new ArrayList(stack);
        stack2.remove(0);

        stack.get(0).trigger(solo);
        assertNotEquals(stack, stack2);
    }
}