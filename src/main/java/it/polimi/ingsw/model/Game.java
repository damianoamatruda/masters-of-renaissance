package it.polimi.ingsw.model;

import it.polimi.ingsw.model.leadercards.*;
import it.polimi.ingsw.model.resourcetypes.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Base game class containing the general components of the "game box", as well as some attributes shared by all players
 * that can be easily accessed from the outside.
 */
public class Game {
    /** Reference to the "Development Card Grid", from which development cards can be "bought". */
    protected final DevCardGrid devCardGrid;

    /** Reference to the "Market Board", from which resources can be "bought". */
    protected final Market market;

    /** Reference to the collection from which all the player's data can be accessed. */
    protected final List<Player> players;

    /** Maps the Vatican report tile to the corresponding state of activation. Boolean value represents
     * whether or not the Vatican report is already over. */
    protected final Map<Integer,Boolean> activatedVaticanSections;

    /** Maps the number of tile to the bonus progressive victory points earned. */
    protected final Map<Integer, Integer> yellowTiles;

    /** Maps the tile of a Vatican report to two values:
     * <ol>
     *     <li>The first tile of the same Vatican Section, which needs to be reached in order to earn bonus points;</li>
     *     <li>The corresponding amount of bonus points that will be rewarded to the players after the Report is over.</li>
     * </ol>
     */
    protected final Map<Integer, Integer[]> vaticanSections;

    /** Number of the last reachable faith track tile by a player. */
    protected final int maxFaithPointsCount;

    /** Progressive number of the current turn. */
    protected int turns = 1;

    /** Flag that indicates the Game is about to end. */
    protected boolean lastTurn;

    /**
     * Constructor of Game instances
     *
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
     * @param vaticanSections               map of the vatican sections
     * @param yellowTiles                   map of the faith tiles which will give bonus points at the end
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
                int playerMaxObtainableDevCards,
                Map<Integer, Integer[]> vaticanSections,
                Map<Integer, Integer> yellowTiles) {
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new RuntimeException();
        if (playerLeadersCount > 0 && nicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new RuntimeException();

        /* Choose a random 1st player */
        List<String> shiftedNicknames = new ArrayList<>(nicknames);
        if (!shiftedNicknames.isEmpty()) {
            int randomIndex = (new Random()).nextInt(shiftedNicknames.size());
            for (int i = 0; i < randomIndex; i++)
                shiftedNicknames.add(shiftedNicknames.remove(0));
        }

        /* Create the players and assign their initial random leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(leaderCards);
        Collections.shuffle(shuffledLeaderCards);
        this.players = new ArrayList<>();
        for (int i = 0; i < shiftedNicknames.size(); i++)
            this.players.add(new Player(
                    shiftedNicknames.get(i),
                    shuffledLeaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i+1)),
                    i == 0,
                    playerWarehouseShelvesCount,
                    playerDevSlotsCount,
                    playerMaxObtainableDevCards));

        this.devCardGrid = new DevCardGrid(devCards, devGridLevelsCount, devGridColorsCount);

        if (marketResources.isEmpty() || marketColsCount == 0)
            this.market = null;
        else
            this.market=new Market(marketResources, marketColsCount);

        /* Generate vatican sections and yellow tiles */
        this.vaticanSections = vaticanSections;
        this.yellowTiles = yellowTiles;

        activatedVaticanSections = new HashMap<>() {{
            for (int i : vaticanSections.keySet())
                put(i, false);
        }};

        this.maxFaithPointsCount = maxFaithPointsCount;
    }

    /**
     * Getter of the count of different Vatican sections in the faith path.
     *
     * @return  the count of different Vatican Sections
     */
    public int getVaticanSectionsCount() {
        return vaticanSections.size();
    }

    /**
     * Getter of all the players who joined the game at the beginning.
     *
     * @return  the list of players (including who disconnected after)
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Getter of the current number of turn.
     *
     * @return  the current turn number
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Getter of the game development card grid.
     *
     * @return  the development card grid
     */
    public DevCardGrid getDevCardGrid() {
        return devCardGrid;
    }

    /**
     * Getter of the game market.
     *
     * @return  the market
     */
    public Market getMarket() {
        return market;
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over.
     *
     * @return  <code>true</code> if the game is over; <code>false</code> otherwise.
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
     * Method called after a faith marker has been moved ahead, checks for available Vatican reports.
     *
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
     * Method called after a card has been bought, checks if the maximum number of buyable cards has been reached.
     *
     * @param player            the player that bought the development card
     * @param maxCardsReached   <code>true</code> if the maximum number of buyable cards has been reached;
     *                          <code>false</code> otherwise.
     */
    public void onAddToDevSlot(Player player, boolean maxCardsReached) {
        // TODO: Re-implement this check
        if (maxCardsReached) lastTurn = true;
    }

    /**
     * Action performed after a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     *
     * @return  the next player that has to play a turn
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
     * Proceeds to calculate the remaining points and chooses a winner.
     */
    private void setWinnerPlayer() {
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
     * Sums the points based on the number of resources left at the end of the game.
     */
    private void sumResourcesVictoryPoints() {
        for(Player p : players){
            p.incrementVictoryPoints(p.getNumOfResources()/5);
        }
    }

    /**
     * Sums the points earned based on the last yellow tile that has been reached.
     */
    private void sumPointsFromYellowTiles() {
        int lastYellowTileReached;
        for(Player p : players){
            lastYellowTileReached = yellowTiles.keySet().stream()
                    .filter(n -> n <= p.getFaithPoints())
                    .reduce(0, (a, b) -> Integer.max(a, b));
            p.incrementVictoryPoints(yellowTiles.get(lastYellowTileReached));
        }
    }

    /**
     * Getter of the variable that maps the Vatican report tile to the corresponding state of activation.
     *
     * @return  the activated Vatican sections
     */
    public Map<Integer, Boolean> getActivatedVaticanSections() {
        return activatedVaticanSections;
    }

    /**
     * Getter of the tiles of Vatican reports.
     *
     * @return  the Vatican sections
     */
    public Map<Integer, Integer[]> getVaticanSections() {
        return vaticanSections;
    }

    /**
     * Getter of the variable that maps the number of tile to the bonus progressive victory points earned.
     *
     * @return  the yellow titles
     */
    public Map<Integer, Integer> getYellowTiles() {
        return yellowTiles;
    }
}
