package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.DevCardColor;
import it.polimi.ingsw.leadercards.LeaderCard;
import it.polimi.ingsw.resourcetypes.*;

import java.util.*;
import java.util.stream.Collectors;

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

    /** Number of columns in the market grid. */
    private static final int MARKET_COLS_COUNT = 4;

    /** Number of coin resources in the market. */
    private static final int MARKET_COIN_COUNT = 2;

    /** Number of faith resources in the market. */
    private static final int MARKET_FAITH_COUNT = 1;

    /** Number of servant resources in the market. */
    private static final int MARKET_SERVANT_COUNT = 2;

    /** Number of shield resources in the market. */
    private static final int MARKET_SHIELD_COUNT = 2;

    /** Number of stone resources in the market. */
    private static final int MARKET_STONE_COUNT = 2;

    /** Number of zero resources in the market. */
    private static final int MARKET_ZERO_COUNT = 4;

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

    /** Default constructor of Game (Will not be used by the controller to create Game) */
    protected Game(){}

    /** Constructor of Game instances
     * @param nicknames the list of nicknames of players who joined
     */
    public Game(List<String> nicknames){
        if (nicknames.size() > MAX_PLAYERS_COUNT)
            throw new RuntimeException();
        // TODO: Implement assignment of the 4 initial leader cards
        this.players=nicknames.stream()
                .map(nickname ->
                        new Player(this, nickname, new ArrayList<>(),nicknames.indexOf(nickname) == 0))
                .collect(Collectors.toList());
        this.devGrid=new ArrayList<>(); // TODO: Implement creation of the dev grid
        this.market=new Market(new HashMap<>() {{
            put(Coin.getInstance(), MARKET_COIN_COUNT);
            put(Faith.getInstance(), MARKET_FAITH_COUNT);
            put(Servant.getInstance(), MARKET_SERVANT_COUNT);
            put(Shield.getInstance(), MARKET_SHIELD_COUNT);
            put(Stone.getInstance(), MARKET_STONE_COUNT);
            put(Zero.getInstance(), MARKET_ZERO_COUNT);
        }}, MARKET_COLS_COUNT);
        vaticanSections = new HashMap<Integer, Integer[]>(){{
            put(8, new Integer[]{5, 2});
            put(16, new Integer[]{12, 3});
            put(24, new Integer[]{19, 4});
        }};
    }

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
        Player temp = players.get(0);
        do {
            players.remove(0);
            players.add(temp);
            temp = players.get(0);
        }while(!temp.isActive());
        return temp;
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
        List<List<DevelopmentCard>> top = new ArrayList<>();
        for(int i = 0; i < devGrid.size(); i++){
            top.add(devGrid.get(i)
                    .stream()
                    .map(deck -> deck.peek())
                    .collect(Collectors.toList()));
        }
        return top;
    }

    /**
     * A player buys a card of a given color and level
     * @param player    the player that wants to buy a card
     * @param color     the color of the card to be bought
     * @param level     the level of the card to be bought
     */
    public void takeDevCard(Player player, DevCardColor color, int level) {
        // TODO: Implement
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over
     * @return true if game is over
     */
    public boolean hasEnded() {
        // TODO: Implement
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
        // TODO: Implement
    }

    /**
     * Proceeds to calculate the remaining points and chooses a winner
     */
    private void setWinnerPlayer(){
        int maxPts = players.stream()
                .mapToInt(Player::getVictoryPoints)
                .max()
                .getAsInt();

        List<Player> winners = players.stream()
                .filter(p -> p.getVictoryPoints() == maxPts)
                .collect(Collectors.toList());

        for(Player p : winners)
            p.setWinner(true);
    }

    /**
     * Sums the points based on the number of resources left at the end of the game
     */
    private void sumResourcesVictoryPoints(){
        for(Player p : players){
            p.incrementVictoryPoints(p.getNumOfResources()/5);
        }
    }

    /**
     * Sums the points earned based on the last yellow tile that has been reached
     */
    private void sumPointsFromYellowTiles(){
        int lastYellowTileReached;
        for(Player p : players){
            lastYellowTileReached = yellowTiles.keySet().stream()
                    .filter(n -> n <= p.getFaithPoints())
                    .reduce(0, (a, b) -> Integer.max(a, b));
            p.incrementVictoryPoints(yellowTiles.get(lastYellowTileReached));
        }
    }
}
