package it.polimi.ingsw.server.model;

/**
 * Exception thrown when a player tries to obtain bonus resources but has already done it.
 */
public class CannotChooseException extends Exception {
    /**
     * Class constructor.
     * 
     * @param choosenElement either "leader cards" or "resources"
     */
    public CannotChooseException(String choosenElement) {
        super(String.format("Cannot choose starting %s again, choice already made.", choosenElement);
    }
}
