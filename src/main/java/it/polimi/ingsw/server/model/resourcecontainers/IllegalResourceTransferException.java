package it.polimi.ingsw.server.model.resourcecontainers;

/**
 * Exception thrown when a resource cannot be added or removed.
 */
public class IllegalResourceTransferException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     * @see Exception
     */
    public IllegalResourceTransferException(String message) {
        super(message);
    }
}
