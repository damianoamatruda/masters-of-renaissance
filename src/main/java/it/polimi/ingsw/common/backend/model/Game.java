package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a game of Masters of Renaissance. It contains the general components of the "game box", as well
 * as some attributes shared by the players that can be easily accessed from the outside.
 */
public class Game extends EventDispatcher {
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

    /** The "Faith Track", where vatican sections can be activated. */
    protected final FaithTrack faithTrack;

    /** The "Development Card Grid", from which development cards can be "bought". */
    protected final DevCardGrid devCardGrid;

    /** The "Market Board", from which resources can be "bought". */
    protected final Market market;

    /** Number of development cards a player can have, before triggering the end of the game. */
    protected final int maxObtainableDevCards;

    /** The development card colors used in the game. */
    protected List<DevCardColor> devCardColors;

    /** The resource types used in the game. */
    protected List<ResourceType> resourceTypes;

    /** Flag that indicates the Game is about to end. */
    protected boolean lastRound;

    /** Flag that indicates the Game has ended. */
    protected boolean ended;

    /** The number of development slots for each player. */
    protected int devSlotsCount;

    /**
     * Constructor of Game instances.
     *
     * @param players               the list of nicknames of players who joined
     * @param devCardColors         the list of development card colors
     * @param resourceTypes         the list of resource types
     * @param leaderCards           the list of leader cards
     * @param developmentCards      the list of development cards
     * @param resContainers         the list of resource containers
     * @param productions           the list of productions
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     */
    public Game(List<Player> players, List<DevCardColor> devCardColors, List<ResourceType> resourceTypes,
                List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                List<ResourceContainer> resContainers, List<ResourceTransactionRecipe> productions,
                DevCardGrid devCardGrid, Market market, FaithTrack faithTrack, int maxObtainableDevCards,
                int devSlotsCount) throws IllegalArgumentException {
        if (players.isEmpty())
            throw new IllegalArgumentException("Empty players list");

        this.players = new ArrayList<>(players);
        this.leaderCards = List.copyOf(leaderCards);
        this.developmentCards = List.copyOf(developmentCards);
        this.resContainers = List.copyOf(resContainers);
        this.productions = List.copyOf(productions);
        this.faithTrack = faithTrack;
        this.devCardGrid = devCardGrid;
        this.market = market;

        this.resourceTypes = resourceTypes;
        this.devCardColors = devCardColors;

        this.maxObtainableDevCards = maxObtainableDevCards;
        this.devSlotsCount = devSlotsCount;
        this.ended = false;

        this.players.forEach(p -> p.addEventListener(MVEvent.class, this::dispatch));
        this.leaderCards.forEach(l -> l.addEventListener(MVEvent.class, this::dispatch));
        this.resContainers.forEach(c -> c.addEventListener(MVEvent.class, this::dispatch));
        this.devCardGrid.addEventListener(MVEvent.class, this::dispatch);
        this.market.addEventListener(MVEvent.class, this::dispatch);
        this.faithTrack.addEventListener(MVEvent.class, this::dispatch);
    }

    public void dispatchState(View view, Player player, boolean isMandatoryActionDone) {
        dispatch(new UpdateGame(
                view,
                players.stream().map(p -> p.reduce(p.equals(player))).toList(),
                devCardColors.stream().map(DevCardColor::reduce).toList(),
                resourceTypes.stream().map(ResourceType::reduce).toList(),
                leaderCards.stream().map(LeaderCard::reduce).toList(),
                developmentCards.stream().map(DevelopmentCard::reduce).toList(),
                resContainers.stream().map(ResourceContainer::reduce).toList(),
                productions.stream().map(ResourceTransactionRecipe::reduce).toList(),
                null, /* actionTokens not sent */
                faithTrack.reduce(),
                market.reduce(),
                devCardGrid.reduce(),
                devSlotsCount,
                isSetupDone(),
                getCurrentPlayer().getNickname(),
                getInkwellPlayer().getNickname(),
                getWinnerPlayer().map(Player::getNickname).orElse(null),
                0, /* blackPoints */
                lastRound,
                ended,
                isMandatoryActionDone));
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
        ended = true;
        setWinnerPlayer();
    }

    /**
     * Returns whether the game has ended.
     *
     * @return <code>true</code> if the game has ended; <code>false</code> otherwise.
     */
    public boolean isEnded() {
        return ended;
    }

    /**
     * Method called after a faith marker has been moved ahead, checks for available Vatican reports.
     */
    public void activateVaticanSections() {
        int maxFaithPoints = getMaxFaithPoints();
        faithTrack.getVaticanSectionReport(maxFaithPoints).ifPresent(vaticanSection -> vaticanSection.activate(players));
        if (maxFaithPoints >= faithTrack.getMaxFaithPointsCount())
            setLastRound();
    }

    public void updatePtsFromYellowTiles(Player player, int advancement) {
        Optional<FaithTrack.YellowTile> reached = faithTrack.getLastReachedYellowTile(player.getFaithPoints());
        Optional<FaithTrack.YellowTile> last = faithTrack.getLastReachedYellowTile(player.getFaithPoints() - advancement);
        if (!reached.equals(last)) {
            int newPoints = reached.map(FaithTrack.YellowTile::getVictoryPoints).orElse(0);
            int oldPoints = last.map(FaithTrack.YellowTile::getVictoryPoints).orElse(0);
            player.incrementVictoryPoints(newPoints - oldPoints);
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

        dispatch(new UpdateLastRound());
    }

    /**
     * Action performed after a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     */
    public void onTurnEnd() {
        if (players.stream().noneMatch(Player::isActive))
            return;

        do {
            players.add(players.remove(0));
            if (lastRound && players.get(0).hasInkwell()) {
                end();
                return;
            }
        } while (!players.get(0).isActive());

        dispatch(new UpdateCurrentPlayer(getCurrentPlayer().getNickname()));
    }

    /**
     * Check if the last necessary setup move has been made.
     */
    public void onPlayerSetupDone() {
        if (isSetupDone())
            dispatch(new UpdateSetupDone());
    }

    /**
     * @return whether the game's setup phase is finished. The answer also depends on the players' status, inactive
     * players are not accounted for.
     */
    public boolean isSetupDone() {
        return players.stream().filter(Player::isActive).map(Player::getSetup).allMatch(PlayerSetup::isDone);
    }

    /**
     * Returns the player with the inkwell.
     *
     * @return the first player
     */
    public Player getInkwellPlayer() {
        return players.stream().filter(Player::hasInkwell).findAny().orElseThrow();
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
     * @return the game's action tokens
     */
    public List<ActionToken> getActionTokens() {
        return List.of();
    }

    /**
     * Returns Lorenzo's faith marker position.
     *
     * @return number of tile reached by Lorenzo
     */
    public int getBlackPoints() {
        throw new RuntimeException("getBlackPoints called on a multiplayer game: method not implemented.");
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
        throw new RuntimeException("isBlackWinner called on a multiplayer game: method not implemented.");
    }

    /**
     * @return the highest amount of faith points owned by any of the Player's game
     */
    public int getMaxFaithPoints() {
        return players.stream().mapToInt(Player::getFaithPoints).max().orElse(0);
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

        /* In case of a draw, the first player in order becomes the winner */
        winners.stream().findFirst().ifPresent(p -> {
            p.setWinner();
            dispatch(new UpdateGameEnd(p.getNickname()));
        });
    }

    /**
     * Sums the points based on the number of resources left at the end of the game.
     */
    private void sumResourcesVictoryPoints() {
        for (Player p : players)
            p.incrementVictoryPoints(p.getResourcesCount() / 5);
    }
}
