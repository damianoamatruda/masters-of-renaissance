package it.polimi.ingsw.common.events.mvevents.Errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrNewGame implements MVEvent {
    private final boolean isInvalidPlayersCount;

    /**
     * @param isInvalidPlayersCount true if the requested players count is invalid,
     *                              false if the new game request came from an invalid player
     *                              (not the first in the lobby)
     */
    public ErrNewGame(boolean isInvalidPlayersCount) {
        this.isInvalidPlayersCount = isInvalidPlayersCount;
    }

    /**
     * @return the isInvalidPlayersCount
     */
    public boolean isInvalidPlayersCount() {
        return isInvalidPlayersCount;
    }
}
