package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/** General action error. */
public class ErrAction extends ViewEvent {
    private final ErrActionReason reason;

    /**
     * @param view
     * @param reason
     */
    public ErrAction(View view, ErrActionReason reason) {
        super(view);
        this.reason = reason;
    }

    public ErrActionReason getReason() {
        return reason;
    }

    public enum ErrActionReason {
        LATE_SETUP_ACTION,
        EARLY_MANDATORY_ACTION,
        LATE_MANDATORY_ACTION,
        EARLY_TURN_END,
        GAME_ENDED,
        NOT_CURRENT_PLAYER
    }
}
