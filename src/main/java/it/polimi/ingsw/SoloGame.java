package it.polimi.ingsw;

import java.util.Collections;
import java.util.List;

/**
 * This class represents the Solo version of the game, and contains all the extra functionality
 */
public class SoloGame extends GameDecorator{
    /** The stack of action tokens to be activated at the end of the player's turn */
    private List<ActionToken> actionTokens;

    /** The "marker" of Lorenzo il Magnifico on the faith track */
    private int blackPoints;

    /**
     * Initializes the solo game with the following parameters.
     * @param game          the wrappee to be extended with solo functionality
     * @param actionTokens  the stack of tokens, of which the top token is activated after each turn
     */
    public SoloGame(Game game, List<ActionToken> actionTokens){
        super(game);
        this.actionTokens = actionTokens;
        blackPoints = 0;
    }

    /**
     * This action is triggered by certain type(s) of token.
     * Shuffles and resets the stack
     */
    public void shuffleActionTokens(){
        Collections.shuffle(actionTokens);
    }

    /**
     * Advances Lorenzo's marker on the faith track by one, then checks for Vatican Report
     */
    public void incrementBlackPoints(){
        blackPoints += 1;
        wrappee.onIncrement(blackPoints);
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over
     * @return  true if game is over
     */
    @Override
    public boolean hasEnded(){
        return false;
    }

    /**
     * Triggered after the player concludes a turn.
     * This is Lorenzo's turn: a token will be activated
     */
    @Override
    public void onTurnEnd() {

    }

    /**
     * Retrieves and activates the top token of the Lorenzo's stack
     */
    private void takeActionToken(){
        ActionToken token = actionTokens.get(0);
        token.trigger(this);
        actionTokens.add(token);
    }

    /** Returns Lorenzo's faith marker position
     * @return  number of tile reached by Lorenzo */
    public int getBlackPoints(){
        return blackPoints;
    }
}
