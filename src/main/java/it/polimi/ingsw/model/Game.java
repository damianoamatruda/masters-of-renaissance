package it.polimi.ingsw.model;

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
     * Constructor of Game instances.
     *
     * @param players               the list of nicknames of players who joined
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param maxFaithPointsCount   number of the last reachable faith track tile by a player
     * @param vaticanSections       map of the vatican sections
     * @param yellowTiles           map of the faith tiles which will give bonus points at the end
     */
    public Game(List<Player> players, DevCardGrid devCardGrid, Market market, int maxFaithPointsCount,
                Map<Integer, Integer[]> vaticanSections, Map<Integer, Integer> yellowTiles) {
        this.players = players;
        this.devCardGrid = devCardGrid;
        this.market = market;
        this.vaticanSections = vaticanSections;
        this.yellowTiles = yellowTiles;
        this.activatedVaticanSections = new HashMap<>() {{
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
            p.incrementVictoryPoints(p.getResourcesCount()/5);
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
