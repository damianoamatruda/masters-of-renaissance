package it.polimi.ingsw.common.backend.model.resourcetransactions;

/**
 * Exception used in validating resource transaction requests' containers.
 */
public class IllegalResourceTransactionContainersException extends IllegalResourceTransactionException {
    private final boolean isInput;
    private final String resType;
    private final int replacedCount, shelvesChoiceResCount;
    private final boolean isIllegalDiscardedOut;

    /**
     * Class constructor.
     *
     * @param isInput               whether the error refers to the input of a production or the output
     * @param resType               the resource type the count of which is wrong in the replaced recipe
     * @param replacedCount         the count of resources in the replaced map
     * @param shelvesChoiceResCount the count of resources in the shelves mapping
     */
    public IllegalResourceTransactionContainersException(boolean isInput, String resType, int replacedCount, int shelvesChoiceResCount, boolean isIllegalDiscardedOut) {
        this.isInput = isInput;
        this.resType = resType;
        this.replacedCount = replacedCount;
        this.shelvesChoiceResCount = shelvesChoiceResCount;
        this.isIllegalDiscardedOut = isIllegalDiscardedOut;
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * @return whether the exception was thrown due to an illegal discard value (typically negative)
     */
    public boolean isIllegalDiscardedOut() {
        return isIllegalDiscardedOut;
    }

    /**
     * @return the resType
     */
    public String getResType() {
        return resType;
    }

    /**
     * @return the shelvesChoiceResCount
     */
    public int getShelvesChoiceResCount() {
        return shelvesChoiceResCount;
    }

    /**
     * @return the replacedCount
     */
    public int getReplacedCount() {
        return replacedCount;
    }
}
