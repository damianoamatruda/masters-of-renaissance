package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.*;
import it.polimi.ingsw.leadercards.*;
import it.polimi.ingsw.resourcetypes.*;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.security.cert.CollectionCertStoreParameters;
import java.util.*;
import java.util.stream.Collectors;

/** Base game class containing the general components of the "game box",
 * as well as some attributes shared by all players that can be easily accessed from the outside
 */
public class Game {
    /** Maximum number of players that can connect to the same game instance */
    private static final int MAX_PLAYERS_COUNT=4;

    /** Number of "Vatican Sections" that can be entered throughout the game */
    private static final int VATICAN_SECTIONS_COUNT=3;

    /** All the cards that are still not bought by any player */
    protected Map<DevCardColor, List<Stack<DevelopmentCard>>> devGrid;

    /** Number of distinct rows of separate decks that represent different development card levels */
    protected final int devGridLevelsCount;

    /** Number of distinct columns of separate decks that represent different development card colors */
    protected final int devGridColorsCount;

    /** Reference to the "Market Board", from which resources can be "bought" */
    protected final Market market;

    /** Reference to the collection from which all the player's data can be accessed */
    protected List<Player> players;

    /** Variable that maps the Vatican report tile to the corresponding state of activation
     * Boolean value represents whether or not the Vatican report is already over */
    private Map<Integer,Boolean> activatedVaticanSections;

    /** Maps the number of tile to the bonus progressive victory points earned */
    private final Map<Integer, Integer> yellowTiles;

    /** Maps the tile of a Vatican report to two values:
     * 1) The first tile of the same Vatican Section, which needs to be reached in order to earn bonus points;
     * 2) The corresponding amount of bonus points that will be rewarded to the players after the Report is over */
    private final Map<Integer, Integer[]> vaticanSections;

    /** Number of the last reachable faith track tile by a player */
    protected final int maxFaithPointsCount;

    /** Progressive number of the current turn */
    protected int turns = 1;

    /** Flag that indicates the Game is about to end */
    protected boolean lastTurn;

    // /** Default constructor of Game (Will not be used by the controller to create Game) */
    // protected Game(){}

    /** Constructor of Game instances
     * @param nicknames                     the list of nicknames of players who joined
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
     */
    public Game(List<String> nicknames,
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
                int playerMaxObtainableDevCards) {
        if (nicknames.size() > MAX_PLAYERS_COUNT)
            throw new RuntimeException();
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new RuntimeException();
        if (playerLeadersCount > 0 && nicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new RuntimeException();

        /* Create the players and assign their initial random leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);
        this.players = new ArrayList<>();
        for (int i = 0; i < nicknames.size(); i++)
            this.players.add(new Player(
                    nicknames.get(i),
                    shuffledLeaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i+1)),
                    i == 0, playerWarehouseShelvesCount, playerDevSlotsCount, playerMaxObtainableDevCards));

        this.devGrid=new HashMap<>();
        this.devGridLevelsCount = devGridLevelsCount;
        this.devGridColorsCount = devGridColorsCount;

        if (devCards.isEmpty() || devGridColorsCount == 0)
            this.devGrid = null;
        else {
            for (DevelopmentCard card : devCards) {
                if (!devGrid.keySet().contains(card.getColor()))
                    devGrid.put(card.getColor(), new ArrayList<>() {{
                        add(0,null);
                        for (int i = 1; i <= devGridLevelsCount; i++)
                            add(i, new Stack<>());
                    }});

                List<Stack<DevelopmentCard>> column = devGrid.get(card.getColor());
                Stack<DevelopmentCard> deck = column.get(card.getLevel());
                deck.push(card);
            }
            for (DevCardColor column : devGrid.keySet()) {
                for (int cardLevel = 1; cardLevel <= devGridLevelsCount; cardLevel++)
                    Collections.shuffle(devGrid.get(column).get(cardLevel));
            }
        }


        if (marketResources.isEmpty() || marketColsCount == 0)
            this.market = null;
        else
            this.market=new Market(marketResources, marketColsCount);

        vaticanSections = new HashMap<>(){{ //TODO
            put(8, new Integer[]{5, 2});
            put(16, new Integer[]{12, 3});
            put(24, new Integer[]{19, 4});
        }};

        activatedVaticanSections = new HashMap<>(){{    //TODO
            put(8, false);
            put(16, false);
            put(24, false);
        }};

        yellowTiles = new HashMap<>(){{ //TODO
            put(3, 1);
            put(6, 2);
            put(9, 4);
            put(12, 6);
            put(15, 9);
            put(18, 12);
            put(21, 16);
            put(24, 20);
        }};

        this.maxFaithPointsCount = maxFaithPointsCount;
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
    public int getDevGridLevelsCount() {
        return devGridLevelsCount;
    }

    /**
     * Getter of the number of different colors a card can have (= columns of the set of buyable cards)
     * @return the number of different card colors
     */
    public int getDevGridColorsCount() {
        return devGridColorsCount;
    }

    /**
     * Getter of the count of different Vatican sections in the faith path
     * @return the count of different Vatican Sections
     */
    public static int getVaticanSectionsCount() {
        return VATICAN_SECTIONS_COUNT;
    }

    /**
     * Getter of all the players who joined the game at the beginning
     * @return the list of players (including who disconnected after)
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
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
        for(DevCardColor color : devGrid.keySet()){
            top.add(devGrid.get(color)
                    .stream()
                    .map(deck -> deck.peek())
                    .collect(Collectors.toList()));
        }
        return top;
    }

    /**
     * A player buys a card of a given color and level
     * @param game                 the game the player is playing in
     * @param player               the player that wants to buy a card
     * @param color                the color of the card to be bought
     * @param level                the level of the card to be bought
     * @param position             the position of the dev slot where to put the development card
     * @param strongboxes          a map of the strongboxes where to take the storable resources
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

        if(trackPoints == maxFaithPointsCount)
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
}
