package it.polimi.ingsw.common.events.mvevents;

/**
 * Current player state update.
 */
public class UpdateCurrentPlayer implements MVEvent {
    /** The nickname of the new current player. */
    private final String player;

    /**
     * Class constructor.
     *
     * @param player the nickname of the new current player
     */
    public UpdateCurrentPlayer(String player) {
        this.player = player;
    }

    /**
     * @return the nickname of the new current player
     */
    public String getPlayer() {
        return player;
    }
}
