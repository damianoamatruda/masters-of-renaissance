package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.*;
import it.polimi.ingsw.leadercards.DepotLeader;
import it.polimi.ingsw.leadercards.DiscountLeader;
import it.polimi.ingsw.leadercards.LeaderCard;
import it.polimi.ingsw.resourcetypes.*;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

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
    private int turns = 1;

    /** Flag that indicates the Game is about to end */
    private boolean lastTurn;

    /** Reference to the "Market Board", from which resources can be "bought" */
    private Market market;

    /** All the cards that are still not bought by any player */
    protected Map<DevCardColor, List<Stack<DevelopmentCard>>> devGrid;

    /** Default constructor of Game (Will not be used by the controller to create Game) */
    protected Game(){}

    /** Constructor of Game instances
     * @param nicknames the list of nicknames of players who joined
     */
    public Game(List<String> nicknames){
        if (nicknames.size() > MAX_PLAYERS_COUNT)
            throw new RuntimeException();
        // TODO: Implement assignment of the 4 initial leader cards
        List<LeaderCard> devCards = getLeaderCards();
        List<DevelopmentCard> leaderCards = getDevCards();

        this.players=nicknames.stream()
                .map(nickname -> new Player(nickname, new ArrayList<>(), nicknames.indexOf(nickname) == 0))
                .collect(Collectors.toList());

        this.devGrid=new HashMap<>(); // TODO: Implement creation of the dev grid
        this.market=new Market(new HashMap<>(){{
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
        activatedVaticanSections = new HashMap<>(){{
            put(8, false);
            put(16, false);
            put(24, false);
        }};
        yellowTiles = new HashMap<>(){{
            put(3, 1);
            put(6, 2);
            put(9, 4);
            put(12, 6);
            put(15, 9);
            put(18, 12);
            put(21, 16);
            put(24, 20);
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

//    /**
//     * Appends the current player as last of the list, and gets the next (active) player from the head
//     * If next player is inactive, the operation is repeated until an active player is found
//     * @return the next player that has to play a turn
//     */
//    public Player nextPlayer() {
//        Player temp = players.get(0);
//        do {
//            players.remove(0);
//            players.add(temp);
//            temp = players.get(0);
//        }while(!temp.isActive());
//        return temp;
//    }

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
        for(DevCardColor color : devGrid.keySet()){
        //for(int i = 0; i < devGrid.size(); i++){
            top.add(devGrid.get(color)
                    .stream()
                    .map(deck -> deck.peek())
                    .collect(Collectors.toList()));
        }
        return top;
    }

    /**
     * A player buys a card of a given color and level
     * @param game      the game the player is playing in
     * @param player    the player that wants to buy a card
     * @param color     the color of the card to be bought
     * @param level     the level of the card to be bought
     * @throws Exception           Bought card cannot fit in chosen player slot
     * @throws Exception           error while player was depositing the card
     * @throws EmptyStackException No cards available with given color and level
     */
    public void buyDevCard(Game game, Player player, DevCardColor color, int level, int position,
                            Map<Strongbox, Map<ResourceType, Integer>> strongboxes)
            throws Exception, EmptyStackException {

        DevelopmentCard card = devGrid.get(color).get(level).pop();
        try {
            boolean maxCardsReached = player.addToDevSlot(game, position, card, strongboxes);
            if(maxCardsReached) lastTurn = true;
        }
        catch (Exception e){
            devGrid.get(color).get(level).push(card);
            throw new Exception();
        }
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over
     * @return true if game is over
     */
    public boolean hasEnded() {
        if(lastTurn){
            setWinnerPlayer();
            return true;
        }
        turns++;
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
            if(p.getFaithPoints()>=currentSection[0])
                p.incrementVictoryPoints(currentSection[1]);

        activatedVaticanSections.put(trackPoints,true);

        if(trackPoints == Player.getMaxFaithPointsCount())
            lastTurn = true;
    }

    /**
     * Action performed after a player ends the turn.
     * This method appends the current player as last of the list, and gets the next (active) player from the head
     * If next player is inactive, the operation is repeated until an active player is found
     * @return the next player that has to play a turn
     */
    public Player onTurnEnd() {
        Player temp = players.get(0);
        do {
            players.remove(0);
            players.add(temp);
            temp = players.get(0);
        }while(!temp.isActive());
        return temp;
    }

    /**
     * Proceeds to calculate the remaining points and chooses a winner
     */
    private void setWinnerPlayer(){
        sumPointsFromYellowTiles();
        sumResourcesVictoryPoints();
        for(Player p : players)
            p.sumCardsVictoryPoints();

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

    /**
     * Returns the list of all possible develompent cards.
     * @return  list of development cards
     */
    private static List<DevelopmentCard> getDevCards() {
        return new ArrayList<>(Arrays.asList(
                /* 1 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                false),
                        1),
                /* 2 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                false),
                        1),
                /* 3 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                false),
                        1),
                /* 4 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                false),
                        1),
                /* 5 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Stone.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                false),
                        2),
                /* 6 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Coin.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                false),
                        2),
                /* 7 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Stone.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                false),
                        2),
                /* 8 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Stone.getInstance(), 1),
                                entry(Coin.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                false),
                        2),
                /* 9 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                false),
                        3),
                /* 10 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                false),
                        3),
                /* 11 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                false),
                        3),
                /* 12 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                false),
                        3),
                /* 13 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 2),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        4),
                /* 14 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 2),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        4),
                /* 15 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 2),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        4),
                /* 16 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 2),
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        4),
                /* 17 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2)),
                                false),
                        5),
                /* 18 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2)),
                                false),
                        5),
                /* 19 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2)),
                                false),
                        5),
                /* 20 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2)),
                                false),
                        5),
                /* 21 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3)),
                                false),
                        6),
                /* 22 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 3)),
                                false),
                        6),
                /* 23 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 3)),
                                false),
                        6),
                /* 24 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3),
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3)),
                                false),
                        6),
                /* 25 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        7),
                /* 26 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        7),
                /* 27 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        7),
                /* 28 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        7),
                /* 29 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3),
                                entry(Coin.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        8),
                /* 30 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3),
                                entry(Shield.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        8),
                /* 31 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3),
                                entry(Stone.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        8),
                /* 32 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3),
                                entry(Servant.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        8),
                /* 33 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        9),
                /* 34 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        9),
                /* 35 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        9),
                /* 36 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)),
                                false),
                        9),
                /* 37 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 5),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        10),
                /* 38 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 5),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        10),
                /* 39 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 5),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        10),
                /* 40 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 5),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)),
                                false),
                        10),
                /* 41 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)),
                                false),
                        11),
                /* 42 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)),
                                false),
                        11),
                /* 43 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)),
                                false),
                        11),
                /* 44 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)),
                                false),
                        11),
                /* 45 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 4),
                                entry(Coin.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Shield.getInstance(), 1)),
                                false),
                        12),
                /* 46 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 4),
                                entry(Shield.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Servant.getInstance(), 1)),
                                false),
                        12),
                /* 47 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 4),
                                entry(Stone.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 3)),
                                false),
                        12),
                /* 48 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 4),
                                entry(Servant.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 3)),
                                false),
                        12)
        ));
    }

    /**
     * Returns the list of all possible leader cards.
     * @return  list of leader cards
     */
    private static List<LeaderCard> getLeaderCards() {
        // TODO: Implement
        return new ArrayList<>(Arrays.asList(
                /* 49 */
                /* 50 */
                /* 51 */
                /* 52 */
                /* 53 */
                /* 54 */
                /* 55 */
                /* 56 */
                /* 57 */
                /* 58 */
                /* 59 */
                /* 60 */
                /* 61 */
                /* 62 */
                /* 63 */
                /* 64 */
        ));
    }
}
