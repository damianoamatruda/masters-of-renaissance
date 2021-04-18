package it.polimi.ingsw.server.model.cardrequirements;

/**
 * Exception signaling that the player being examined does not meet the specified requirements.
 *
 * @see CardRequirement
 * @see it.polimi.ingsw.server.model.Player
 */
public class RequirementsNotMetException extends Exception {
    /**
     * Class constructor.
     *
     * @param message the message that describes the event.
     */
    public RequirementsNotMetException(String message) {
        super(message);
    }
}
