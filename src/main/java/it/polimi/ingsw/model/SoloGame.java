package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class represents the Solo version of the game, and contains all the extra functionality.
 */
public class SoloGame extends Game {
    /** The stack of action tokens to be activated at the end of the player's turn. */
    private final List<ActionToken> actionTokens;

    /** The "marker" of Lorenzo il Magnifico on the faith track. */
    private int blackPoints;

    /** Flag that determines whether Lorenzo has won the game. */
    private boolean blackWinner;

    /**
     * Initializes the solo game with the following parameters.
     * @param player                      the nickname of the player who joined
     * @param devCardGrid                   the development card grid
     * @param market                        the resource market
     * @param maxFaithPointsCount           number of the last reachable faith track tile by a player
     * @param vaticanSections               map of the vatican sections
     * @param yellowTiles                   map of the faith tiles which will give bonus points at the end
     * @param actionTokens                  the stack of tokens, of which the top token is activated after each turn
     */
    public SoloGame(Player player, DevCardGrid devCardGrid, Market market, int maxFaithPointsCount,
                    Map<Integer, Integer[]> vaticanSections, Map<Integer, Integer> yellowTiles,
                    List<ActionToken> actionTokens) {
        super(List.of(player), devCardGrid, market, maxFaithPointsCount, vaticanSections, yellowTiles);
        this.actionTokens = actionTokens;
        blackPoints = 0;
        blackWinner = false;
    }

    /**
     * This action is triggered by certain type(s) of token. Shuffles and resets the stack.
     */
    public void shuffleActionTokens() {
        Collections.shuffle(actionTokens);
    }

    /**
     * Advances Lorenzo's marker on the faith track by one, then checks for Vatican Report.
     */
    public void incrementBlackPoints() {
        blackPoints += 1;
        super.onIncrement(blackPoints);
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over.
     *
     * @return  <code>true</code> if the game is over; <code>false</code> otherwise.
     */
    @Override
    public boolean hasEnded() {
        // TODO: Don't access protected attributes
        if(blackPoints == maxFaithPointsCount || devCardGrid.grid.size() < devCardGrid.getColorsCount()){
            setBlackWinner();
            return true;
        }
        else return super.hasEnded();
    }

    /**
     * Triggered after the player concludes a turn.
     *
     * This is Lorenzo's turn: a token will be activated
     */
    @Override
    public Player onTurnEnd() {
        ActionToken token = actionTokens.get(0);
        token.trigger(this);
        actionTokens.add(token);

        return super.onTurnEnd();
    }


    /**
     * Returns Lorenzo's faith marker position.
     *
     * @return  number of tile reached by Lorenzo
     */
    public int getBlackPoints() {
        return blackPoints;
    }

    /**
     * Says whether Lorenzo has won the game or not.
     *
     * @return  <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     */
    public boolean isBlackWinner() {
        return blackWinner;
    }

    /**
     * Declares Lorenzo as winner.
     */
    public void setBlackWinner() {
        this.blackWinner = true;
    }
}
