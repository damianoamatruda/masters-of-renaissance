package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrInexistentEntity implements MVEvent {
    private final String reason;

    /**
     * @param reason
     */
    public ErrInexistentEntity(String reason) {
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
}
