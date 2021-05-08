package it.polimi.ingsw.server.model.actiontokens;

import it.polimi.ingsw.server.model.SoloGame;

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
