package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.*;

/**
 * This class represents a game of Masters of Renaissance. It contains the general components of the "game box", as well
 * as some attributes shared by the players that can be easily accessed from the outside.
 */
public class Game extends ModelObservable {
    /** Reference to the collection from which all the player's data can be accessed. */
    protected final List<Player> players;

    /** The leader cards used in the game. */
    protected final List<LeaderCard> leaderCards;

    /** The development cards used in the game. */
    protected final List<DevelopmentCard> developmentCards;

    /** The resource containers used in the game. */
    protected final List<ResourceContainer> resContainers;

    /** The productions used in the game. */
    protected final List<ResourceTransactionRecipe> productions;

    /** The "Development Card Grid", from which development cards can be "bought". */
    protected final DevCardGrid devCardGrid;

    /** The "Market Board", from which resources can be "bought". */
    protected final Market market;

    /** The "Faith Track", where vatican sections can be activated. */
    protected final FaithTrack faithTrack;

    /** Number of the last reachable faith track tile by a player. */
    protected final int maxFaithPointsCount;

    /** Number of development cards a player can have, before triggering the end of the game. */
    protected final int maxObtainableDevCards;

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
     * @param leaderCards           the list of leader cards
     * @param developmentCards      the list of development cards
     * @param resContainers         the list of resource containers
     * @param productions           the list of productions
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param maxFaithPointsCount   the number of the last reachable faith track tile by a player
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     */
    public Game(List<Player> players, List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                List<ResourceContainer> resContainers, List<ResourceTransactionRecipe> productions,
                DevCardGrid devCardGrid, Market market, FaithTrack faithTrack, int maxFaithPointsCount,
                int maxObtainableDevCards) {
        this.players = new ArrayList<>(players);
        this.leaderCards = List.copyOf(leaderCards);
        this.developmentCards = List.copyOf(developmentCards);
        this.resContainers = List.copyOf(resContainers);
        this.productions = List.copyOf(productions);
        this.devCardGrid = devCardGrid;
        this.market = market;
        this.faithTrack = faithTrack;
        this.maxFaithPointsCount = maxFaithPointsCount;
        this.maxObtainableDevCards = maxObtainableDevCards;
        this.ended = false;
    }

    public void register(View o) {
        this.observers.add(o);
        this.players.forEach(obj -> obj.addObserver(o));
        this.leaderCards.forEach(obj -> obj.addObserver(o));
        // this.developmentCards.forEach(obj -> obj.addObserver(o)); // technically unneeded
        this.resContainers.forEach(obj -> obj.addObserver(o));
        this.devCardGrid.addObserver(o);
        this.market.addObserver(o);
        this.faithTrack.addObserver(o);
    }

    public void addObserver(View o, String nickname) {
        register(o);

        Player p = players.stream().filter(pl -> pl.getNickname().equals(nickname)).findFirst().orElseThrow();

        List<Integer> leaders = p.getLeaders().stream().map(Card::getId).toList();
        List<Integer> shelves = p.getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList();
        int strongbox = p.getStrongbox().getId();

        notify(o, new UpdateGameStart(
            players.stream().map(Player::getNickname).toList(),
            market.reduce(),
            devCardGrid.reduce(),
            leaderCards.stream().map(LeaderCard::reduce).toList(),
            developmentCards,
            resContainers.stream().map(ResourceContainer::reduce).toList(),
            productions,
            leaders,
            shelves,
            strongbox,
            null));
    }

    public void resume(View o, String nickname) {
        register(o);

        Player p = players.stream().filter(pl -> pl.getNickname().equals(nickname)).findFirst().orElseThrow();

        List<Integer> leaders = p.getLeaders().stream().map(Card::getId).toList();
        List<Integer> shelves = p.getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList();
        int strongbox = p.getStrongbox().getId();

        notify(o, new UpdateGameResumed(
                players.stream().map(Player::getNickname).toList(),
                market.reduce(),
                devCardGrid.reduce(),
                leaderCards.stream().map(LeaderCard::reduce).toList(),
                developmentCards,
                resContainers.stream().map(ResourceContainer::reduce).toList(),
                productions,
                leaders,
                shelves,
                strongbox,
                null,
                p.hasChosenLeaders(), p.hasChosenResources()
        ));
    }

    public void removeObserver(View o) {
        this.observers.remove(o);
        this.players.forEach(obj -> obj.removeObserver(o));
        this.leaderCards.forEach(obj -> obj.removeObserver(o));
        this.resContainers.forEach(obj -> obj.removeObserver(o));
        this.devCardGrid.removeObserver(o);
        this.market.removeObserver(o);
        this.faithTrack.removeObserver(o);
    }

    public Optional<LeaderCard> getLeaderById(int id) {
        return leaderCards.stream().filter(l -> l.getId() == id).findFirst();
    }

    public Optional<ResourceContainer> getShelfById(int id) {
        return resContainers.stream().filter(l -> l.getId() == id).findFirst();
    }

    public Optional<ResourceTransactionRecipe> getProductionById(int id) {
        return productions.stream().filter(l -> l.getId() == id).findFirst();
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
    private void end() {
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
    public void onIncrementFaithPoints(int faithPoints) {
        FaithTrack.VaticanSection vaticanSection = faithTrack.getVaticanSectionReport(faithPoints);
        if (vaticanSection != null && !vaticanSection.isActivated()) {
            for (Player p : players)
                if (p.getFaithPoints() >= vaticanSection.getFaithPointsBeginning())
                    p.incrementVictoryPoints(vaticanSection.getVictoryPoints());
            vaticanSection.activate();
        }

        if (faithPoints == maxFaithPointsCount)
            setLastRound();
    }

    public void updatePtsFromYellowTiles(Player player, int advancement) {
        Optional<FaithTrack.YellowTile> reached = faithTrack.getLastReachedYellowTile(player.getFaithPoints());
        Optional<FaithTrack.YellowTile> last = faithTrack.getLastReachedYellowTile(player.getFaithPoints() - advancement);
        if(!reached.equals(last)) {
            int newPts = reached.map(FaithTrack.YellowTile::getVictoryPoints).orElse(0);
            int oldPts = last.map(FaithTrack.YellowTile::getVictoryPoints).orElse(0);
            player.incrementVictoryPoints(newPts - oldPts);
        }
    }

    /**
     * Method called after resources have been discarded by a player.
     *
     * @param player   the player who has discarded resources
     * @param quantity the quantity of discarded resources
     */
    public void onDiscardResources(Player player, int quantity) {
        players.stream()
                .filter(p -> !p.equals(player))
                .forEach(p -> p.incrementFaithPoints(this, quantity));
    }

    /**
     * Method called after a leader card has been discarded by a player. The player receives one faith point.
     *
     * @param player the player who has discarded the leader card
     */
    public void onDiscardLeader(Player player) {
        player.incrementFaithPoints(this, 1);
    }

    /**
     * Method called after a card has been bought, checks if the maximum number of buyable cards has been reached.
     *
     * @param obtainedDevCards the number of all development cards obtained by the player
     */
    public void onAddToDevSlot(int obtainedDevCards) {
        if (obtainedDevCards == maxObtainableDevCards)
            setLastRound();
    }

    private void setLastRound() {
        lastRound = true;

        notifyBroadcast(new UpdateLastRound());
    }

    /**
     * Action performed after a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     *
     * @throws NoActivePlayersException all players are set to inactive
     */
    public void onTurnEnd() throws NoActivePlayersException {
        if (players.stream().noneMatch(Player::isActive))
            throw new NoActivePlayersException();

        Player nextPlayer;
        do {
            players.add(players.remove(0));
            nextPlayer = players.get(0);
        } while (!nextPlayer.isActive());

        notifyBroadcast(new UpdateCurrentPlayer(getCurrentPlayer().getNickname()));

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
     * Getter of the current number of completed rounds.
     *
     * @return the current round number
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Returns Lorenzo's faith marker position.
     *
     * @return number of tile reached by Lorenzo
     */
    public int getBlackPoints() {
        // return 0;
        throw new RuntimeException("getBlackPoints called on a mutiplayer game: method not implemented.");
    }

    /**
     * Returns the player who won, if the winner is a player.
     *
     * @return the optional player
     */
    public Optional<Player> getWinnerPlayer() {
        return players.stream().filter(Player::isWinner).findAny();
    }

    /**
     * Returns whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     */
    public boolean isBlackWinner() {
        // return false;
        throw new RuntimeException("isBlackWinner called on a mutiplayer game: method not implemented.");
    }

    /**
     * Getter of the leader cards used in the game.
     *
     * @return the list of leader cards
     */
    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * Getter of the development cards used in the game.
     *
     * @return the list of development cards
     */
    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * Getter of the resource containers used in the game.
     *
     * @return the list of resource containers
     */
    public List<ResourceContainer> getResContainers() {
        return resContainers;
    }

    /**
     * Getter of the productions used in the game.
     *
     * @return the list of productions
     */
    public List<ResourceTransactionRecipe> getProductions() {
        return productions;
    }

    /**
     * Proceeds to calculate the remaining points and chooses a winner.
     */
    private void setWinnerPlayer() {
        sumResourcesVictoryPoints();

        int maxPts = players.stream()
                .mapToInt(Player::getVictoryPoints)
                .max()
                .orElse(0);

        List<Player> winners = players.stream()
                .filter(p -> p.getVictoryPoints() == maxPts)
                .toList();

        winners.stream().findFirst().ifPresent(p -> {
            p.setWinner();
            notifyBroadcast(new UpdateGameEnd(p.getNickname(), computeVictoryPointsMap(players)));
        });
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
            faithTrack.getLastReachedYellowTile(p.getFaithPoints())
                .ifPresent(lastReachedYellowTile -> p.incrementVictoryPoints(lastReachedYellowTile.getVictoryPoints()));
        }
    }

    public Map<String, Integer> computeVictoryPointsMap(List<Player> players) {
        Map<String, Integer> vp = new HashMap<>();
        
        for (Player p : players)
        vp.put(p.getNickname(), p.getVictoryPoints());
        
        return vp;
    }
}
