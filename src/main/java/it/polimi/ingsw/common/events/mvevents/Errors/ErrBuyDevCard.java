package it.polimi.ingsw.common.events.mvevents.Errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrBuyDevCard implements MVEvent {
    private final boolean isStackEmpty;

    /**
     * @param isStackEmpty
     */
    public ErrBuyDevCard(boolean isStackEmpty) {
        this.isStackEmpty = isStackEmpty;
    }

    /**
     * @return the isStackEmpty
     */
    public boolean isStackEmpty() {
        return isStackEmpty;
    }
}
