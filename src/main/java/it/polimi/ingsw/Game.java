package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/** Base game class containing the general components of the "game box",
 * as well as some attributes shared by all players that can be easily accessed from the outside
 */
public class /*Base*/Game /*implements IGame*/{
    /** Maximum number of players that can connect to the same game instance */
    private static final int MAX_PLAYERS_COUNT=4;

    /** Number of distinct rows of separate decks that represent different development card levels */
    private static final int DEV_GRID_LEVELS_COUNT=3;

    /** Number of distinct columns of separate decks that represent different development card colors */
    private static final int DEV_GRID_COLORS_COUNT=4;

    /** Number of "Vatican Sections" that can be entered throughout the game */
    private static final int VATICAN_SECTIONS_COUNT=3;

    /** Reference to the collection from which all the player's data can be accessed */
    private List<Player> players;

    /** Variable that maps the Vatican report tile to the corresponding state of activation
     * Boolean value represents whether or not the Vatican report is already over */
    private Map<Integer,Boolean> activatedVaticanSections;

    /** Maps the number of tile to the bonus progressive victory points earned */
    private Map<Integer,Integer> yellowTiles;

    /** Maps the tile of a Vatican report to two values:
     * 1) The first tile of the same Vatican Section, which needs to be reached in order to earn bonus points;
     * 2) The corresponding amount of bonus points that will be rewarded to the players after the Report is over */
    private Map<Integer,Integer[]> vaticanSections;

    /** Progressive number of the current turn */
    private int turns;

    /** Reference to the "Market Board", from which resources can be "bought" */
    private Market market;

    /** All the cards that are still not bought by any player */
    private List<List<Stack<DevelopmentCard>>> devGrid;

    //

    /**
     * Getter of the maximum number of players that can connect to a game
     * @return the maximum number of players
     */
    public static int getMaxPlayersCount() {
        return MAX_PLAYERS_COUNT;
    }

    /**
     * Getter of the number of rows containing cards of different levels (from 1 to the max level)
     * @return the maximum level of a card (= the number of rows)
     */
    public static int getDevGridLevelsCount() {
        return DEV_GRID_LEVELS_COUNT;
    }

    /**
     * Getter of the number of different colors a card can have (= columns of the set of buyable cards)
     * @return the number of different card colors
     */
    public static int getDevGridColorsCount() {
        return DEV_GRID_COLORS_COUNT;
    }

    /**
     * Getter of the count of different Vatican sections in the faith path
     * @return the count of different Vatican Sections
     */
    public static int getVaticanSectionsCount() {
        return VATICAN_SECTIONS_COUNT;
    }

    /**
     * Getter of the number of different players who joined the game (including those who might disconnect)
     * @return Number of players involved in the game
     */
    public int getPlayersCount() {
        return players.size();
    }

    /**
     * Getter of all the players who joined the game at the beginning
     * @return the list of players (including who disconnected after)
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Appends the current player as last of the list, and gets the next (active) player from the head
     * If next player is inactive, the operation is repeated until an active player is found
     * @return the next player that has to play a turn
     */
    public Player nextPlayer() {
        return null;
    }

    /**
     * Getter of the current number of turn
     * @return the current turn number
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Getter of the game market
     * @return the market
     */
    public Market getMarket() {
        return market;
    }

    /**
     * Retrieves the top card of each deck (the cards that can be bought during this turn)
     * @return the top card of each deck
     */
    public List<List<DevelopmentCard>> peekDevCards() {
        return null;
    }

    /**
     * A player buys a card of a given color and level
     * @param player    the player that wants to buy a card
     * @param color     the color of the card to be bought
     * @param level     the level of the card to be bought
     */
    public void takeDevCard(Player player, DevCardColor color, int level) {

    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over
     * @return true if game is over
     */
    public boolean hasEnded() {
        return false;
    }

    /**
     * Method called after a faith marker has been moved ahead, checks for available Vatican reports
     * @param trackPoints   the faith marker (points) of whoever just moved ahead
     */
    public void onIncrement(int trackPoints) {
        Integer[] currentSection = vaticanSections.get(trackPoints);
        if(currentSection == null || activatedVaticanSections.get(trackPoints)) return;
        for(Player p : players)
            if(p.getFaithPoints()>currentSection[0])
                p.incrementVictoryPoints(currentSection[0]);
        if(trackPoints == Player.getMaxFaithPointsCount())
            setWinnerPlayer();
    }

    /**
     * Action performed after a player ends the turn
     */
    public void onTurnEnd() {

    }

    /**
     * Proceeds to calculate the remaining points and chooses a winner
     */
    private void setWinnerPlayer(){ }

    /**
     * Sums the points based on the number of resources left at the end of the game
     */
    private void sumResourcesVictoryPoints(){}

    /**
     * Sums the points earned based on the last yellow tile that has been reached
     */
    private void sumPointsFromYellowTiles(){}
}
