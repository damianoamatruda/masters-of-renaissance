package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for FaithTrack.
 */
class FaithTrackTest {
    FaithTrack track;

    /**
     * Setup of the game instance, and assign the needed references.
     */
    @BeforeEach
    void setup() {
        track = new FaithTrack(Set.of(), Set.of(
                new FaithTrack.YellowTile(3, 1)
        ));
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
        assertNull(track.getLastReachedYellowTile(2));
    }
}
