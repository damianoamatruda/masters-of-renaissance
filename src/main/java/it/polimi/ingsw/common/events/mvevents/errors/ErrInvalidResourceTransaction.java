package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrInvalidResourceTransaction extends ViewEvent {
    private final boolean isInput;
    private final boolean isReplacement;
    private final ReplacementReason reason;

    public enum ReplacementReason {
        NEGATIVE_VALUES,
        ILLEGAL_STORABLE,
        ILLEGAL_NON_STORABLE,
        EXCLUDED
    }

    /**
     * @param view
     * @param isInput
     * @param isNonStorable
     * @param isExcluded
     */
    public ErrInvalidResourceTransaction(View view, boolean isInput, boolean isReplacement, String reason) {
        super(view);
        this.isInput = isInput;
        this.isReplacement = isReplacement;
        this.reason = ReplacementReason.valueOf(reason);
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }
    /**
     * @return 
     */
    public boolean isReplacement() {
        return isReplacement;
    }
    /**
     * @return
     */
    public ReplacementReason getReason() {
        return reason;
    }
}
