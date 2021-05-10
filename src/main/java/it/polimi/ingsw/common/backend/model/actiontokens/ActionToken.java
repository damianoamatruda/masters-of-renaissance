package it.polimi.ingsw.common.backend.model.actiontokens;

import it.polimi.ingsw.common.backend.model.SoloGame;

/**
 * This interface represents an action token, and will be used only in Solo games.
 */
public interface ActionToken {
    /**
     * Triggers the effect of a specific token.
     *
     * @param game the current game in which the token is activated
     */
    void trigger(SoloGame game);

    /**
     * @return the ID of the token
     */
    int getId();
}