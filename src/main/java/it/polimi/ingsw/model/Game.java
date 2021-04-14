package it.polimi.ingsw.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Base game class containing the general components of the "game box", as well as some attributes shared by all players
 * that can be easily accessed from the outside.
 */
public class Game {
    /** The "Development Card Grid", from which development cards can be "bought". */
    protected final DevCardGrid devCardGrid;

    /** The "Market Board", from which resources can be "bought". */
    protected final Market market;

    /** The "Faith Track", where vatican sections can be activated. */
    protected final FaithTrack faithTrack;

    /** Reference to the collection from which all the player's data can be accessed. */
    protected final List<Player> players;

    /** Number of the last reachable faith track tile by a player. */
    protected final int maxFaithPointsCount;

    /** Number of development cards a player can have, before triggering the end of the game. */
    private final int maxObtainableDevCards;

    /** Progressive number of the current turn. */
    protected int turns = 1;

    /** Flag that indicates the Game is about to end. */
    protected boolean lastTurn;

    /**
     * Constructor of Game instances.
     * @param players               the list of nicknames of players who joined
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param maxFaithPointsCount   the number of the last reachable faith track tile by a player
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the game
     */
    public Game(List<Player> players, DevCardGrid devCardGrid, Market market, FaithTrack faithTrack,
                int maxFaithPointsCount, int maxObtainableDevCards) {
        this.players = players;
        this.devCardGrid = devCardGrid;
        this.market = market;
        this.faithTrack = faithTrack;
        this.maxFaithPointsCount = maxFaithPointsCount;
        this.maxObtainableDevCards = maxObtainableDevCards;
    }

    /**
     * Proceeds to sum the remaining points and decide a winner after the game is over.
     *
     * @return  <code>true</code> if the game is over; <code>false</code> otherwise.
     */
    public boolean hasEnded() {
        if (lastTurn) {
            setWinnerPlayer();
            return true;
        }
        turns++;
        return false;
    }

    /**
     * Method called after a faith marker has been moved ahead, checks for available Vatican reports.
     *
     * @param player        the player who has just moved ahead
     * @param faithPoints   the faith marker (points) of whoever has just moved ahead
     */
    public void onIncrement(Player player, int faithPoints) {
        FaithTrack.VaticanSection vaticanSection = faithTrack.getVaticanSectionReport(faithPoints);
        if (vaticanSection != null && !vaticanSection.isActivated()) {
            for (Player p : players)
                if (p.getFaithPoints() >= vaticanSection.getFaithPointsBeginning())
                    p.incrementVictoryPoints(vaticanSection.getVictoryPoints());
            vaticanSection.setActivated(true);
        }

        if(faithPoints == maxFaithPointsCount)
            lastTurn = true;
    }

    /**
     * Method called after a card has been bought, checks if the maximum number of buyable cards has been reached.
     *
     * @param player            the player that bought the development card
     * @param obtainedDevCards  the number of all development cards obtained by the player
     */
    public void onAddToDevSlot(Player player, int obtainedDevCards) {
        if (obtainedDevCards == maxObtainableDevCards)
            lastTurn = true;
    }

    /**
     * Action performed after a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     *
     * @return                      the next player that has to play a turn
     * @throws AllInactiveException all players are set to inactive
     */
    public Player onTurnEnd() throws AllInactiveException{
        if (players.stream().filter(p -> p.isActive()).count() == 0) throw new AllInactiveException();

        Player temp = players.get(0);
        do {
            players.remove(0);
            players.add(temp);
            temp = players.get(0);
        } while (!temp.isActive());
        return temp;

    }

//    /**
//     * Getter of the current player.
//     *
//     * @return  the player now playing
//     */
//    public Player getCurrentPlayer {
//        return players.get(0);
//    }

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
     * Getter of the game faith track.
     *
     * @return  the development card grid
     */
    public FaithTrack getFaithTrack() {
        return faithTrack;
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
        for (Player p : players)
            p.incrementVictoryPoints(p.getResourcesCount() / 5);
    }

    /**
     * Sums the points earned based on the last yellow tile that has been reached.
     */
    private void sumPointsFromYellowTiles() {
        for (Player p : players) {
            FaithTrack.YellowTile lastReachedYellowTile = faithTrack.getLastReachedYellowTile(p.getFaithPoints());
            if (lastReachedYellowTile != null)
                p.incrementVictoryPoints(lastReachedYellowTile.getVictoryPoints());
        }
    }
}
