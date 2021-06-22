package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrResourceReplacement extends ViewEvent {
    private final boolean isInput;
    private final boolean isNonStorable;
    private final boolean isExcluded;

    /**
     * @param view
     * @param isInput
     * @param isNonStorable
     * @param isExcluded
     * @param replacedCount
     * @param blanks
     */
    public ErrResourceReplacement(View view, boolean isInput, boolean isNonStorable, boolean isExcluded) {
        super(view);
        this.isInput = isInput;
        this.isNonStorable = isNonStorable;
        this.isExcluded = isExcluded;
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }
    /**
     * @return the isExcluded
     */
    public boolean isExcluded() {
        return isExcluded;
    }
    /**
     * @return the isNonStorable
     */
    public boolean isNonStorable() {
        return isNonStorable;
    }
}
