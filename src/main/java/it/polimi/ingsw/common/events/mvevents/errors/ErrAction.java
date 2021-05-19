package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** General action error. */
public class ErrAction implements MVEvent {
    private final ErrActionReason reason;

    public enum ErrActionReason {
        LATESETUPACTION,
        EARLYMANDATORYACTION,
        LATEMANDATORYACTION,
        EARLYTURNEND,
        ENDEDGAME,
        NOTCURRENTPLAYER
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
