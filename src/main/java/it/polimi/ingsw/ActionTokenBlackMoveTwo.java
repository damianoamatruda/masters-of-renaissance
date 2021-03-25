package it.polimi.ingsw;

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
        game.incrementBlackCross();
        game.incrementBlackCross();
    }
}
