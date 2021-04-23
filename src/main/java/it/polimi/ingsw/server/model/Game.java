package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a game of Masters of Renaissance. It contains the general components of the "game box", as well
 * as some attributes shared by the players that can be easily accessed from the outside.
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

    /** Progressive number of the current round. */
    protected int rounds = 0;

    /** Flag that indicates the Game is about to end. */
    protected boolean lastRound;

    /** Flag that indicates the Game has ended. */
    protected boolean ended;

    /**
     * Constructor of Game instances.
     *
     * @param players               the list of nicknames of players who joined
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param maxFaithPointsCount   the number of the last reachable faith track tile by a player
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     *                              game
     */
    public Game(List<Player> players, DevCardGrid devCardGrid, Market market, FaithTrack faithTrack,
                int maxFaithPointsCount, int maxObtainableDevCards) {
        this.players = new ArrayList<>(players);
        this.devCardGrid = devCardGrid;
        this.market = market;
        this.faithTrack = faithTrack;
        this.maxFaithPointsCount = maxFaithPointsCount;
        this.maxObtainableDevCards = maxObtainableDevCards;
        this.ended = false;
    }

    /**
     * Returns whether the game is at its last round.
     *
     * @return <code>true</code> if the game is at its last round; <code>false</code> otherwise.
     */
    public boolean isLastRound() {
        return lastRound;
    }

    /**
     * Proceeds to sum the remaining points, decide a winner and end the game.
     */
    @Deprecated
    public void end() {
        setWinnerPlayer();
        ended = true;
    }

    /**
     * Returns whether the game has ended.
     *
     * @return <code>true</code> if the game has ended; <code>false</code> otherwise.
     */
    public boolean hasEnded() {
        return ended;
    }

    /**
     * Method called after a faith marker has been moved ahead, checks for available Vatican reports.
     *
     * @param faithPoints the faith marker (points) of whoever has just moved ahead
     */
    public void onIncrement(int faithPoints) {
        FaithTrack.VaticanSection vaticanSection = faithTrack.getVaticanSectionReport(faithPoints);
        if (vaticanSection != null && !vaticanSection.isActivated()) {
            for (Player p : players)
                if (p.getFaithPoints() >= vaticanSection.getFaithPointsBeginning())
                    p.incrementVictoryPoints(vaticanSection.getVictoryPoints());
            vaticanSection.setActivated(true);
        }

        if (faithPoints == maxFaithPointsCount)
            lastRound = true;
    }

    /**
     * Method called after a resource has been discarded by a player.
     *
     * @param player the player who discarded the resource
     */
    public void onDiscard(Player player) {
        players.stream()
                .filter(p -> !p.equals(player))
                .forEach(p -> p.incrementFaithPoints(this));
    }

    /**
     * Method called after a card has been bought, checks if the maximum number of buyable cards has been reached.
     *
     * @param obtainedDevCards the number of all development cards obtained by the player
     */
    public void onAddToDevSlot(int obtainedDevCards) {
        if (obtainedDevCards == maxObtainableDevCards)
            lastRound = true;
    }

    /**
     * Action performed after a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     *
     * @throws AllInactiveException all players are set to inactive
     */
    public void onTurnEnd() throws AllInactiveException {
        if (players.stream().noneMatch(Player::isActive))
            throw new AllInactiveException();

        Player nextPlayer;
        do {
            players.add(players.remove(0));
            nextPlayer = players.get(0);
        } while (!nextPlayer.isActive());

        if (nextPlayer.equals(getFirstPlayer()))
            rounds++;

        if (lastRound)
            end();
    }

    /**
     * Returns the player with the inkwell.
     *
     * @return the first player
     */
    public Player getFirstPlayer() {
        return players.stream().filter(Player::hasInkwell).findFirst().orElseThrow();
    }

    /**
     * Returns the player that has to play a turn.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players.get(0);
    }

    /**
     * Getter of all the players who joined the game at the beginning.
     *
     * @return the list of players (including who disconnected after)
     */
    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    /**
     * Getter of the current number of completed rounds.
     *
     * @return the current round number
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Getter of the game development card grid.
     *
     * @return the development card grid
     */
    public DevCardGrid getDevCardGrid() {
        return devCardGrid;
    }

    /**
     * Getter of the game market.
     *
     * @return the market
     */
    public Market getMarket() {
        return market;
    }

    /**
     * Getter of the game faith track.
     *
     * @return the development card grid
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
        for (Player p : players)
            p.sumCardsVictoryPoints();

        int maxPts = players.stream()
                .mapToInt(Player::getVictoryPoints)
                .max()
                .orElse(0);

        List<Player> winners = players.stream()
                .filter(p -> p.getVictoryPoints() == maxPts)
                .toList();

        for (Player p : winners)
            p.setWinner(true);
    }

    /**
     * Returns Lorenzo's faith marker position.
     *
     * @return number of tile reached by Lorenzo
     */
    public int getBlackPoints() {
        return 0;
    }

    /**
     * Says whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     */
    public boolean isBlackWinner() {
        return false;
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
