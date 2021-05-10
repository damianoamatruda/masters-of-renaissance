package it.polimi.ingsw.common.backend.model;

/**
 * Exception thrown when resources from the market cannot be taken with the given options.
 */
public class IllegalMarketTransferException extends Exception {
    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param cause   the cause
     * @see Exception
     */
    public IllegalMarketTransferException(Throwable cause) {
        super("Cannot take the market's resources: ", cause);
    }
}
