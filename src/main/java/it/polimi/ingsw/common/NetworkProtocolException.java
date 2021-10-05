package it.polimi.ingsw.common;

/** Exception signaling an error processing a network message. */
public class NetworkProtocolException extends RuntimeException {
    /**
     * Class constructor.
     *
     * @param message the reason for the exception
     */
    public NetworkProtocolException(String message) {
        super(message);
    }

    /**
     * Class constructor.
     *
     * @param message the reason for the exception
     * @param cause   unhandled exception cause of this exception
     */
    public NetworkProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
