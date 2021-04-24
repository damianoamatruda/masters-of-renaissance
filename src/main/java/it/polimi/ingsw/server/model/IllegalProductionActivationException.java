package it.polimi.ingsw.server.model;

/**
 * Exception thrown when a production cannot be activated with the given options.
 */
public class IllegalProductionActivationException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     * @see Exception
     */
    public IllegalProductionActivationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     * @see Exception
     */
    public IllegalProductionActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
