package it.polimi.ingsw.common.backend.model.cardrequirements;

/**
 * Exception signaling that the player being examined does not meet the specified requirements.
 *
 * @see CardRequirement
 * @see it.polimi.ingsw.common.backend.model.Player
 */
public class CardRequirementsNotMetException extends Exception {
    /**
     * Class constructor.
     *
     * @param type the requirement's type (resource/card)
     * @param msg the message that describes the event.
     */
    public CardRequirementsNotMetException(String type, String msg) {
        super(String.format("Card requirement of type %s not satisfied:\n%s",
            type, msg));
    }

    /**
     * Class constructor.
     *
     * @param type the requirement's type (resource/card)
     * @param e underlying exception
     */
    public CardRequirementsNotMetException(String type, Throwable e) {
        super(String.format("Card requirement of type %s not satisfied", type), e);
    }
}
