package it.polimi.ingsw;

/**
 * Implements one of the possible effects which a token can trigger
 */
public class ActionTokenBlackMoveOneShuffle implements ActionToken{
    /**
     * Triggers the effect of this token: move Black cross ahead by one, then shuffle and reset the stack of tokens
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.incrementBlackPoints();
        game.shuffleActionTokens();
    }
}
