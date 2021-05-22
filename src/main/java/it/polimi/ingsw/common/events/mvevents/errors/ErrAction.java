package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** General action error. */
public class ErrAction implements MVEvent {
    private final ErrActionReason reason;

    public enum ErrActionReason {
        LATE_SETUP_ACTION,
        EARLY_MANDATORY_ACTION,
        LATE_MANDATORY_ACTION,
        EARLY_TURN_END,
        GAME_ENDED,
        NOT_CURRENT_PLAYER
    }
    /**
     * @param reason
     */
    public ErrAction(ErrActionReason reason) {
        this.reason = reason;
    }

    public ErrActionReason getReason() {
        return reason;
    }
}
