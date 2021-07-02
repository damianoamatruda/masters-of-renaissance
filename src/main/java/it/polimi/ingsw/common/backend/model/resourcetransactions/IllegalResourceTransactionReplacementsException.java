package it.polimi.ingsw.common.backend.model.resourcetransactions;

/**
 * Exception used in validating transaction requests' replacements.
 */
public class IllegalResourceTransactionReplacementsException extends IllegalResourceTransactionException {
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
     * @param isInput
     * @param isReplacement
     * @param reason
     */
    public IllegalResourceTransactionReplacementsException(boolean isInput, boolean isReplacement, ReplacementReason reason) {
        this.isInput = isInput;
        this.isReplacement = isReplacement;
        this.reason = reason;
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
