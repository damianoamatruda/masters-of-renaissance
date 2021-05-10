package it.polimi.ingsw.server.model.resourcetransactions;

/**
 * Exception thrown when a resource transaction cannot be activated with the given options.
 */
public class IllegalResourceTransactionActivationException extends IllegalResourceTransactionException {
    // TODO: Delete
    public IllegalResourceTransactionActivationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     * @see Exception
     */
    public IllegalResourceTransactionActivationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param req     the invalid <code>ResourceTransactionRequest</code> that generated the exception
     * @param cause   the cause
     * @see Exception
     */
    public IllegalResourceTransactionActivationException(String message, ResourceTransactionRequest req, Throwable cause) {
        super(String.format("%s\n%s", message, stringify(req)), cause);
    }
}
