package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/**
 * Event signaling an issue with a resource transaction request.
 */
public class ErrInvalidResourceTransaction extends ViewEvent {
    private final boolean isInput;
    private final boolean isReplacement;
    private final ReplacementReason reason;

    /**
     * @param view
     * @param isInput       whether the error refers to the transaction's input
     * @param isReplacement whether the error refers to the replacement resources
     * @param reason        reason detailing the error
     */
    public ErrInvalidResourceTransaction(View view, boolean isInput, boolean isReplacement, String reason) {
        super(view);
        this.isInput = isInput;
        this.isReplacement = isReplacement;
        this.reason = ReplacementReason.valueOf(reason);
    }

    /**
     * @return
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

    public enum ReplacementReason {
        NEGATIVE_VALUES,
        ILLEGAL_STORABLE,
        ILLEGAL_NON_STORABLE,
        EXCLUDED
    }
}
