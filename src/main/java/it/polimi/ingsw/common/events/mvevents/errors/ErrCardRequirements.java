package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrCardRequirements extends ViewEvent {
    private final String reason;

    /**
     * @param reason the reason for the error
     */
    public ErrCardRequirements(View view, String reason) {
        super(view);
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
}
