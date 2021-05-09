package it.polimi.ingsw.server.model.actiontokens;

import it.polimi.ingsw.server.model.SoloGame;

/**
 * Implements one of the possible effects which a token can trigger.
 */
public class ActionTokenBlackMoveTwo implements ActionToken {
    private final int id;

    /**
     * Class constructor.
     * 
     * @param id the ID of the token
     */
    public ActionTokenBlackMoveTwo(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Triggers the effect of this token: move Black cross ahead by two.
     *
     * @param game the current game in which the token is activated
     */
    @Override
    public void trigger(SoloGame game) {
        game.incrementBlackPoints();
        game.incrementBlackPoints();
    }
}
