package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Action token state update. */
public class UpdateActionToken implements MVEvent {
    /** The ID of the token being updated. */
    private final int token;

    /**
     * Class constructor.
     *
     * @param token the ID of the token being updated
     * @param stack the new stack of tokens
     */
    public UpdateActionToken(int token) {
        this.token = token;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the ID of the token being updated
     */
    public int getToken() {
        return token;
    }
}
