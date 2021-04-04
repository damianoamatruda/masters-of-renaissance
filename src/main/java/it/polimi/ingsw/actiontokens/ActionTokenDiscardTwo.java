package it.polimi.ingsw.actiontokens;

import it.polimi.ingsw.SoloGame;
import it.polimi.ingsw.devcardcolors.DevCardColor;

/**
 * Implements one of the possible effects which a token can trigger
 */
public class ActionTokenDiscardTwo implements ActionToken{
    /** the specific color of the cards to be discarded after activation (target color) */
    private DevCardColor discardedColor;

    /**
     * Initializes class and member
     * @param color to be assigned as the "target color"
     */
    public ActionTokenDiscardTwo(DevCardColor color){
        discardedColor = color;
    }

    /**
     * Triggers the effect of this token:
     * disables the first two available cards of a given color, starting from the lowest level
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.discardDevCards(discardedColor, 2);
    }
}
