package it.polimi.ingsw.common.backend.model.resourcetransactions;

/**
 * Exception used in validating resource transaction requests' containers.
 */
public class IllegalResourceTransactionContainersException extends IllegalResourceTransactionException {
    private final String resType;
    private final int replacedCount, shelvesChoiceResCount;
    
    /**
     * Class constructor.
     * 
     * @param resType               the resource type the count of which is wrong in the replaced recipe
     * @param replacedCount         the count of resources in the replaced map
     * @param shelvesChoiceResCount the count of resources in the shelves mapping
     */
    public IllegalResourceTransactionContainersException(String resType, int replacedCount, int shelvesChoiceResCount) {
        this.resType = resType;
        this.replacedCount = replacedCount;
        this.shelvesChoiceResCount = shelvesChoiceResCount;
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
