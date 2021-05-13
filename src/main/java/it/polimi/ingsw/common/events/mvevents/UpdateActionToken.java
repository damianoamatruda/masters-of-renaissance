package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Action token state update. */
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

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the ID of the token being updated
     */
    public int getActionToken() {
        return actionToken;
    }
}
