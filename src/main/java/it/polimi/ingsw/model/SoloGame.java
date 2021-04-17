package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a single-player game of Masters of Renaissance. It contains all the extra functionality.
 */
public class SoloGame extends Game {
    /** The deck of action tokens to be activated at the end of the player's turn. */
    private final List<ActionToken> actionTokens;

    /** The "marker" of Lorenzo il Magnifico on the faith track. */
    private int blackPoints;

    /** Flag that determines whether Lorenzo has won the game. */
    private boolean blackWinner;

    /**
     * Initializes the solo game with the following parameters.
     *
     * @param player                the nickname of the player who joined
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param actionTokens          the deck of tokens, of which the top token is activated after each round
     * @param maxFaithPointsCount   the number of the last reachable faith track tile by a player
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     *                              game
     */
    public SoloGame(Player player, DevCardGrid devCardGrid, Market market, FaithTrack faithTrack,
                    List<ActionToken> actionTokens, int maxFaithPointsCount, int maxObtainableDevCards) {
        super(List.of(player), devCardGrid, market, faithTrack, maxFaithPointsCount, maxObtainableDevCards);
        this.actionTokens = new ArrayList<>(actionTokens);
        this.blackPoints = 0;
        this.blackWinner = false;
    }

    /**
     * This action is triggered by certain type(s) of token. Shuffles and resets the deck.
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
     * Triggered after a round is concluded. Proceeds to sum the remaining points and decide a winner after the game is
     * over.
     *
     * @return <code>true</code> if the game is over; <code>false</code> otherwise.
     */
    @Override
    public boolean hasEnded() {
        if (blackPoints == maxFaithPointsCount || devCardGrid.numOfAvailableColors() < devCardGrid.getColorsCount()) {
            setBlackWinner();
            return true;
        } else return super.hasEnded();
    }

    /**
     * Triggered after the player concludes a turn. This is Lorenzo's turn: a token will be activated.
     *
     * @throws AllInactiveException all players are inactive
     */
    @Override
    public Player onTurnEnd() throws AllInactiveException {
        ActionToken token = actionTokens.get(0);
        token.trigger(this);
        actionTokens.add(token);

        return super.onTurnEnd();
    }

    /**
     * Returns Lorenzo's faith marker position.
     *
     * @return number of tile reached by Lorenzo
     */
    public int getBlackPoints() {
        return blackPoints;
    }

    /**
     * Says whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
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
