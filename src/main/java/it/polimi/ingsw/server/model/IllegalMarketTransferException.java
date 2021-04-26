package it.polimi.ingsw.server.model;

/**
 * Exception thrown when resources from the market cannot be taken with the given options.
 */
public class IllegalMarketTransferException extends Exception {
    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     * @see Exception
     */
    public IllegalMarketTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
