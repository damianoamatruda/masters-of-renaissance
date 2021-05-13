package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.List;

/** Action token state update. */
public class UpdateActionToken implements MVEvent {
    /** The ID of the token being updated. */
    private final int actionToken;

    /** The new stack of tokens. */
    private final List<Integer> stack; // TODO: Is it to be sent?

    /**
     * Class constructor.
     *
     * @param actionToken the ID of the token being updated
     * @param stack       the new stack of tokens
     */
    public UpdateActionToken(int actionToken, List<Integer> stack) {
        this.actionToken = actionToken;
        this.stack = stack;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the new stack of tokens
     */
    public List<Integer> getStack() {
        return stack;
    }

    /**
     * @return the ID of the token being updated
     */
    public int getActionToken() {
        return actionToken;
    }
}
