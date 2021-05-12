package it.polimi.ingsw.common.backend.model.actiontokens;

import it.polimi.ingsw.common.backend.model.SoloGame;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;

/**
 * Implements one of the possible effects which a token can trigger.
 */
public class ActionTokenBlackMoveOneShuffle implements ActionToken {
    private final int id;

    /**
     * Class constructor.
     * 
     * @param id the ID of the token
     */
    public ActionTokenBlackMoveOneShuffle(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
    
    /**
     * Triggers the effect of this token: move Black cross ahead by one, then shuffle and reset the deck of tokens.
     *
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.incrementBlackPoints(1);
        game.shuffleActionTokens();
    }

    @Override
    public ReducedActionToken reduce() {
        return new ReducedActionToken(getId(), getClass().getSimpleName(), null);
    }
}
