package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for FaithTrack.
 */
class FaithTrackTest {
    Player player;
    Game game;
    FaithTrack track;

    /**
     * Setup of the game instance, and assign the needed references.
     */
    @BeforeEach
    void setup() {
        track = new FaithTrack(Set.of(), Set.of(
                new FaithTrack.YellowTile(3, 1)
        ));
        player = new Player("", true, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0, 0, 0);
        game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, track, 0, 0);
    }

    /**
     * Checks that if a player reaches a yellow tile, that tile appears as the last yellow tile reached.
     */
    @Test
    void playerOnYellowTile() {
        assertEquals(track.getLastReachedYellowTile(3).getFaithPoints(), 3);
    }

    /**
     * Checks the correct last yellow tile reached (when the player has already gone past that tile).
     */
    @Test
    void playerAfterYellowTile() {
        assertEquals(track.getLastReachedYellowTile(4).getFaithPoints(), 3);
    }

    /**
     * Checks that no yellow tile is returned, if the player hasn't reached any.
     */
    @Test
    void playerBeforeYellowTile() {
        assertEquals(track.getLastReachedYellowTile(2), null);
    }
}
