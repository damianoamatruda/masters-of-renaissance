package it.polimi.ingsw.common.backend.model;

/**
 * Exception thrown when all players are disconnected / inactive.
 */
public class NoActivePlayersException extends Exception {
    /** Class constructor */
    public NoActivePlayersException() {
        super("All players have disconnected");
    }
}