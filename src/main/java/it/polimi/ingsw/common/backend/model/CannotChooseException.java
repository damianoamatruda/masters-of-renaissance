package it.polimi.ingsw.common.backend.model;

/**
 * Exception thrown when a player tries to obtain bonus resources but has already done it.
 */
public class CannotChooseException extends RuntimeException {
    /**
     * Class constructor.
     *
     * @param reason the reason for the exception being thrown
     */
    public CannotChooseException(String reason) {
        super(reason);
    }
}
