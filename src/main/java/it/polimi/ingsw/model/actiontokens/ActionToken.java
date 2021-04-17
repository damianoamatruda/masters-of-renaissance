package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.model.SoloGame;

/**
 * This interface represents an action token, and will be used only in Solo games.
 */
@FunctionalInterface
public interface ActionToken {
    /**
     * Triggers the effect of a specific token.
     *
     * @param game the current game in which the token is activated
     */
    void trigger(SoloGame game);
}
