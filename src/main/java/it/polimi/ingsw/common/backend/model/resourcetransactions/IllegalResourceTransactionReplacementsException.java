package it.polimi.ingsw.common.backend.model.resourcetransactions;

/**
 * Exception used in validating transaction requests' replacements.
 */
public class IllegalResourceTransactionReplacementsException extends IllegalResourceTransactionException {
    private final boolean isInput;
    private final boolean isNonStorable;
    private final boolean isExcluded;
    private final int replacedCount, blanks;

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
        boolean isExcluded,
        int replacedCount,
        int blanks) {
        this.isInput = isInput;
        this.isNonStorable = isNonStorable;
        this.isExcluded = isExcluded;
        this.replacedCount = replacedCount;
        this.blanks = blanks;
    }
    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }
    /**
     * @return the blanks
     */
    public int getBlanks() {
        return blanks;
    }
    /**
     * @return the replacedCount
     */
    public int getReplacedCount() {
        return replacedCount;
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
