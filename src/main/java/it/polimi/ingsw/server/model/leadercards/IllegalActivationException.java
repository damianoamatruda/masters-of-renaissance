package it.polimi.ingsw.server.model.leadercards;

/**
 * Exception signaling the activation of a leadercard the requirements of which aren't met by the player activating it.
 *
 * @see it.polimi.ingsw.server.model.cardrequirements.CardRequirement
 * @see LeaderCard
 * @see it.polimi.ingsw.server.model.Player
 */
public class IllegalActivationException extends Exception {
    /**
     * Class constructor.
     *
     * @param msg the message that describes the event.
     */
    public IllegalActivationException(String msg) {
        super(msg);
    }
}
