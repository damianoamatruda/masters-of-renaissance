package it.polimi.ingsw;

/**
 * This interface represents an action token, and will be used only in Solo games
 */
public interface ActionToken {
    /** Triggers the effect of a specific token
     * @param game the current game in which the token is activated
     */
    void trigger(SoloGame game);
}
