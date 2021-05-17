package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrCardRequirements implements MVEvent {
    private final String reason;

    /**
     * @param reason the reason for the error
     */
    public ErrCardRequirements(String reason) {
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
}
