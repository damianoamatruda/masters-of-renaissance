package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/** General action error. */
public class ErrAction extends ViewEvent {
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
}
