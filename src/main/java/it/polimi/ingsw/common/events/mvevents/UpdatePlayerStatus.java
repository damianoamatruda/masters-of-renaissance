package it.polimi.ingsw.common.events.mvevents;

/**
 * Player connection status update.
 */
public class UpdatePlayerStatus implements MVEvent {
    /** The player the new status refers to. */
    private final String player;

    /** <code>true</code> if the player is now active; <code>false</code> otherwise. */
    private final boolean isActive;

    /**
     * Class constructor.
     *
     * @param player   the player the new status refers to
     * @param isActive <code>true</code> if the player is now active; <code>false</code> otherwise.
     */
    public UpdatePlayerStatus(String player, boolean isActive) {
        this.player = player;
        this.isActive = isActive;
    }

    /**
     * @return <code>true</code> if the player is now active; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return the nickname of the player the status of whom changed
     */
    public String getPlayer() {
        return player;
    }
}
