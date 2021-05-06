package it.polimi.ingsw.server.model;

/**
 * Exception thrown when the action requested from the player is illegal in the current state of the game.
 */
public class IllegalActionException extends Exception {
    /**
     * Class constructor.
     * 
     * @param action the illegal action trying to be performed
     */
    public IllegalActionException(String action, String reason) {
        super(String.format("Action '%s' not allowed at this stage of the game. Reason:\n%s", action, reason));
    }
}
