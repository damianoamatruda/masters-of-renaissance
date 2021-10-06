package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/**
 * Event signaling an error relating to a client's request for a new game.
 */
public class ErrNewGame extends ViewEvent {
    private final boolean isInvalidPlayersCount;

    /**
     * @param view
     * @param isInvalidPlayersCount true if the requested players count is invalid, false if the new game request came
     *                              from an invalid player
     */
    public ErrNewGame(View view, boolean isInvalidPlayersCount) {
        super(view);
        this.isInvalidPlayersCount = isInvalidPlayersCount;
    }

    /**
     * @return the isInvalidPlayersCount
     */
    public boolean isInvalidPlayersCount() {
        return isInvalidPlayersCount;
    }
}
