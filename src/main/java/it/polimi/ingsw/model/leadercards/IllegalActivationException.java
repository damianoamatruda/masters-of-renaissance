package it.polimi.ingsw.model.leadercards;

/**
 * Exception signaling the activation of a leadercard the requirements of which
 * aren't met by the player activating it.
 * 
 * @see CardRequirement
 * @see LeaderCard
 * @see Player
 */
public class IllegalActivationException extends Exception {
    public IllegalActivationException(String msg) {
        super(msg);
    }
}
