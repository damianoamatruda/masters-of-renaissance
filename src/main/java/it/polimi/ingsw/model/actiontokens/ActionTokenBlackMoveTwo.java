package it.polimi.ingsw.model.actiontokens;

import it.polimi.ingsw.model.SoloGame;

/**
 * Implements one of the possible effects which a token can trigger
 */
public class ActionTokenBlackMoveTwo implements ActionToken{
    /**
     * Triggers the effect of this token: move Black cross ahead by two
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.incrementBlackPoints();
        game.incrementBlackPoints();
    }
}
