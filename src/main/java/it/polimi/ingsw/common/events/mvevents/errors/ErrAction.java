package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** General action error. */
public class ErrAction implements MVEvent {
    private final int reason;

    /**
     * @param reason ordinal of 0. LATESETUPACTION,
     *                          1. EARLYMANDATORYACTION,
     *                          2. LATEMANDATORYACTION,
     *                          3. EARLYTURNEND,
     *                          4. ENDEDGAME,
     *                          5. NOTCURRENTPLAYER
     */
    public ErrAction(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }
}
