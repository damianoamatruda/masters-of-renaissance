package it.polimi.ingsw.common.events.mvevents;

/**
 * Action token state update.
 */
public class UpdateActionToken implements MVEvent {
    /** The ID of the token being updated. */
    private final int actionToken;

    /**
     * Class constructor.
     *
     * @param actionToken the ID of the token being updated
     */
    public UpdateActionToken(int actionToken) {
        this.actionToken = actionToken;
    }

    /**
     * @return the ID of the token being updated
     */
    public int getActionToken() {
        return actionToken;
    }
}
