package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/**
 * Error related to buying a development card.
 */
public class ErrBuyDevCard extends ViewEvent {
    private final boolean isStackEmpty;

    /**
     * @param view
     * @param isStackEmpty true if the player pointed to an empty stack, false if the card cannot be placed into the
     *                     player's development slot
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
