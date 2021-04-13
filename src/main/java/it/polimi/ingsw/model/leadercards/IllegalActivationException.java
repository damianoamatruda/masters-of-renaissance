package it.polimi.ingsw.model.leadercards;

/**
 * Exception signaling the activation of a leadercard the requirements of which
 * aren't met by the player activating it.
 * 
 * @see it.polimi.ingsw.model.cardrequirements.CardRequirement
 * @see LeaderCard
 * @see it.polimi.ingsw.model.Player
 */
public class IllegalActivationException extends Exception {
    public IllegalActivationException(String msg) {
        super(msg);
    }
}
