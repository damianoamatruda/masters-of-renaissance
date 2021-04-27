package it.polimi.ingsw.server.model;

/**
 * Exception thrown when a player tries to obtain bonus resources but has already done it.
 */
public class CannotChooseException extends Exception {
    /**
     * Class constructor.
     * 
     * @param isResourceChoice whether the illegally repeated choice refers to resources or leader cards.
     */
    public CannotChooseException(boolean isResourceChoice) {
        super(String.format("Cannot choose starting %s again, choice already made.", isResourceChoice ? "resources" : "leaders"));
    }
}
