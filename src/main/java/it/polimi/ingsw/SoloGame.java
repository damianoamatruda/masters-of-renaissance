package it.polimi.ingsw;

import it.polimi.ingsw.actiontokens.ActionToken;
import it.polimi.ingsw.devcardcolors.DevCardColor;

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

    /** Flag that determines whether Lorenzo has won the game */
    private boolean blackWinner;

    /**
     * Initializes the solo game with the following parameters.
     * @param game          the wrappee to be extended with solo functionality
     * @param actionTokens  the stack of tokens, of which the top token is activated after each turn
     */
    public SoloGame(Game game, List<ActionToken> actionTokens){
        super(game);
        this.actionTokens = actionTokens;
        blackPoints = 0;
        blackWinner = false;
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
        if(blackPoints == Player.getMaxFaithPointsCount() || devGrid.size() < getDevGridColorsCount()){
            setBlackWinner();
            return true;
        }
        else return super.hasEnded();
    }

    /**
     * Triggered after the player concludes a turn.
     * This is Lorenzo's turn: a token will be activated
     */
    @Override
    public Player onTurnEnd() {
        ActionToken token = actionTokens.get(0);
        token.trigger(this);
        actionTokens.add(token);

        return super.onTurnEnd();
    }


    /** Returns Lorenzo's faith marker position
     * @return  number of tile reached by Lorenzo */
    public int getBlackPoints(){
        return blackPoints;
    }

    /**
     * Says whether Lorenzo has won the game or not
     * @return  true if Lorenzo is winner of the game
     */
    public boolean isBlackWinner() {
        return blackWinner;
    }

    /**
     *  Declares Lorenzo as winner
     */
    public void setBlackWinner() {
        this.blackWinner = true;
    }

    /**
     * Removes development cards, so that nobody else can purchase them
     *
     * @param color     the color to be discarded
     * @param quantity  the number of cards to be discarded
     */
    public void discardDevCards(DevCardColor color, int quantity){
        int level = 1;
        while(quantity > 0 && level <= 3) {
            DevelopmentCard card = devGrid.get(color).get(level).pop();
        }
    }

}
