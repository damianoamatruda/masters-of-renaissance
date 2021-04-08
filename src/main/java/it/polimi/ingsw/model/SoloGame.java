package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

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
     *
     * @param nickname                      the list of nicknames of players who joined
     * @param leaderCards                   the list of all the leader cards in the game
     * @param playerLeadersCount            number of distinct leader cards given to each player at the beginning of the game
     * @param devCards                      the list of all the development cards in the game
     * @param devGridLevelsCount            number of distinct rows of separate decks that represent different development card levels
     * @param devGridColorsCount            number of distinct columns of separate decks that represent different development card colors
     * @param marketResources               map of the resources to put inside the market
     * @param marketColsCount               number of columns in the market grid
     * @param maxFaithPointsCount           number of the last reachable faith track tile by a player
     * @param playerWarehouseShelvesCount   number of basic shelves inside of each player's warehouse
     * @param playerDevSlotsCount           number of possible player's production slots that can be occupied by development cards
     * @param playerMaxObtainableDevCards   number of development cards each player can have, before triggering the end of the game
     * @param vaticanSections               map of the vatican sections
     * @param yellowTiles                   map of the faith tiles which will give bonus points at the end
     * @param actionTokens                  the stack of tokens, of which the top token is activated after each turn
     */
    public SoloGame(String nickname,
                    List<LeaderCard> leaderCards,
                    int playerLeadersCount,
                    List<DevelopmentCard> devCards,
                    int devGridLevelsCount,
                    int devGridColorsCount,
                    Map<ResourceType, Integer> marketResources,
                    int marketColsCount,
                    int maxFaithPointsCount,
                    int playerWarehouseShelvesCount,
                    int playerDevSlotsCount,
                    int playerMaxObtainableDevCards,
                    Map<Integer, Integer[]> vaticanSections, Map<Integer, Integer> yellowTiles,
                    List<ActionToken> actionTokens
    ){
        super(List.of(nickname), leaderCards, playerLeadersCount, devCards, devGridLevelsCount, devGridColorsCount,
                marketResources, marketColsCount, maxFaithPointsCount, playerWarehouseShelvesCount,
                playerDevSlotsCount, playerMaxObtainableDevCards,
                vaticanSections, yellowTiles);
        this.actionTokens = actionTokens;
        blackPoints = 0;
        blackWinner = false;
    }

    /**
     * This action is triggered by certain type(s) of token. Shuffles and resets the stack.
     */
    public void shuffleActionTokens(){
        Collections.shuffle(actionTokens);
    }

    /**
     * Advances Lorenzo's marker on the faith track by one, then checks for Vatican Report.
     */
    public void incrementBlackPoints(){
        blackPoints += 1;
        super.onIncrement(blackPoints);
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over.
     *
     * @return  <code>true</code> if the game is over; <code>false</code> otherwise.
     */
    @Override
    public boolean hasEnded(){
        if(blackPoints == maxFaithPointsCount || devGrid.size() < getDevGridColorsCount()){
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
    public int getBlackPoints(){
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

    /**
     * Removes development cards, so that nobody else can purchase them.
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
