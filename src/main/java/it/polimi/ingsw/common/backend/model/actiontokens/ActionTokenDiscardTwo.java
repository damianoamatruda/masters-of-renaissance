package it.polimi.ingsw.common.backend.model.actiontokens;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import it.polimi.ingsw.common.backend.model.SoloGame;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;

/**
 * Implements one of the possible effects which a token can trigger.
 */
public class ActionTokenDiscardTwo implements ActionToken {
    private final int id;

    /** The specific color of the cards to be discarded after activation (target color). */
    private final DevCardColor discardedColor;

    /**
     * Class constructor.
     *
     * @param id    the id of this token
     * @param color color to be assigned as the "target color"
     */
    public ActionTokenDiscardTwo(int id, DevCardColor color) {
        discardedColor = color;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
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

    @Override
    public ReducedActionToken reduce() {
        return new ReducedActionToken(getId(), getClass().getSimpleName(), discardedColor.getName());
    }
}
