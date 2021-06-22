package it.polimi.ingsw.common.backend.model.resourcetransactions;

/**
 * Exception used in validating transaction requests' replacements.
 */
public class IllegalResourceTransactionReplacementsException extends IllegalResourceTransactionException {
    private final boolean isInput;
    private final boolean isNonStorable;
    private final boolean isExcluded;

    /**
     * @param isInput
     * @param isNonStorable
     * @param isExcluded
     * @param replacedCount
     * @param blanks
     */
    public IllegalResourceTransactionReplacementsException (
        boolean isInput,
        boolean isNonStorable,
        boolean isExcluded) {
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
