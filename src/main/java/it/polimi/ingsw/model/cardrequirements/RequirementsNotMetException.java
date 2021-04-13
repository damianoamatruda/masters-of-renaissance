package it.polimi.ingsw.model.cardrequirements;

/**
 * Exception signaling that the player being examined does not meet the specified requirements.
 * 
 * @see CardRequirement
 * @see Player
 */
public class RequirementsNotMetException extends Exception {
    /**
     * Class constructor.
     * 
     * @param message   the message that describes the event.
     */
    public RequirementsNotMetException(String message) { super(message); }
}
