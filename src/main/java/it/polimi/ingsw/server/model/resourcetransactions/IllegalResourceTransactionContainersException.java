package it.polimi.ingsw.server.model.resourcetransactions;

/**
 * Exception used in validating resource transaction requests' containers.
 */
public class IllegalResourceTransactionContainersException extends IllegalResourceTransactionException { // not used for checks, instances could be replaced by superclass
    /**
     * Class constructor.
     *
     * @param message the reason for this exception.
     */
    public IllegalResourceTransactionContainersException(String message) {
        super(message);
    }
}
