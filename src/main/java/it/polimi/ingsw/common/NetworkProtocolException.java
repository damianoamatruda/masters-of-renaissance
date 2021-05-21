package it.polimi.ingsw.common;

public class NetworkProtocolException extends RuntimeException {
    public NetworkProtocolException(String message) {
        super(message);
    }

    public NetworkProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
