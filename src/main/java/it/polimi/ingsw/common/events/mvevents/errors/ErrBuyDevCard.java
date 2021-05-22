package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrBuyDevCard extends ViewEvent {
    private final boolean isStackEmpty;

    /**
     * @param view
     * @param isStackEmpty
     */
    public ErrBuyDevCard(View view, boolean isStackEmpty) {
        super(view);
        this.isStackEmpty = isStackEmpty;
    }

    /**
     * @return the isStackEmpty
     */
    public boolean isStackEmpty() {
        return isStackEmpty;
    }
}
