package it.polimi.ingsw.server.model.actiontokens;

import it.polimi.ingsw.server.model.DevCardColor;
import it.polimi.ingsw.server.model.SoloGame;

/**
 * Implements one of the possible effects which a token can trigger.
 */
public class ActionTokenDiscardTwo implements ActionToken {
    /** The specific color of the cards to be discarded after activation (target color). */
    private final DevCardColor discardedColor;

    /**
     * Initializes class and member.
     *
     * @param color color to be assigned as the "target color"
     */
    public ActionTokenDiscardTwo(DevCardColor color) {
        discardedColor = color;
    }

    /**
     * Triggers the effect of this token: disables the first two available cards of a given color, starting from the
     * lowest level.
     *
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.getDevCardGrid().discardDevCards(discardedColor, 2);
    }
}
