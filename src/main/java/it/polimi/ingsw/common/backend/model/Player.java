package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.*;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionContainersException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionReplacementsException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.*;

/**
 * Class dedicated to the storage of the player's data and available operations.
 */
public class Player extends EventDispatcher {
    /** The player's nickname. */
    private final String nickname;

    /** Visible to the view, this indicates whether the player starts first each round. */
    private final boolean inkwell;

    /** The hand of leader cards available to the player. */
    private final List<LeaderCard> leaders;

    /** The player's warehouse, standard container of buyable resources. */
    private final Warehouse warehouse;

    /** The player's strongbox, where all the production output goes. */
    private final Strongbox strongbox;

    /** The collection of cards in each production slot. */
    private final List<Stack<DevelopmentCard>> devSlots;

    /** The base production "recipe". */
    private final ResourceTransactionRecipe baseProduction;

    /** The player's setup. */
    private final PlayerSetup setup;

    /** The player's faith track marker. */
    private int faithPoints;

    /** The player's score. */
    private int victoryPoints;

    /** The player's state of connection to the game. */
    private boolean active;

    /** The flag to be set when a winner has been decided. */
    private boolean winner;

    /**
     * Initializes player's attributes.
     *
     * @param nickname                 the player's nickname to be seen by all the players
     * @param inkwell                  received only by the first player
     * @param leaders                  the leader cards in the player's hand
     * @param warehouse                the player's warehouse
     * @param strongbox                the player's strongbox
     * @param baseProduction           the player's base production
     * @param devSlotsCount            number of possible production slots that can be occupied by development cards
     */
    public Player(String nickname, boolean inkwell, List<LeaderCard> leaders, Warehouse warehouse, Strongbox strongbox,
                  ResourceTransactionRecipe baseProduction, int devSlotsCount, PlayerSetup setup) {
        this.nickname = nickname;
        this.inkwell = inkwell;
        this.leaders = new ArrayList<>(leaders);
        this.warehouse = warehouse;
        this.strongbox = strongbox;
        this.devSlots = new ArrayList<>();
        for (int i = 0; i < devSlotsCount; i++)
            this.devSlots.add(new Stack<>());
        this.baseProduction = baseProduction;
        this.setup = setup;

        this.faithPoints = 0;
        this.victoryPoints = 0;
        this.active = true;
        this.winner = false;
    }

    public void dispatchPublicState(View view) {
        dispatch(new UpdatePlayer(
                view,
                nickname,
                baseProduction.getId(),
                warehouse.getShelves().stream().map(ResourceContainer::getId).toList(),
                strongbox.getId(),
                setup.reduce()));
        dispatch(new UpdateVictoryPoints(view, nickname, victoryPoints));
        dispatch(new UpdateFaithPoints(view, nickname, faithPoints, false));
        for (int devSlotIndex = 0; devSlotIndex < devSlots.size(); devSlotIndex++) {
            Stack<DevelopmentCard> devSlot = devSlots.get(devSlotIndex);
            if (!devSlot.empty())
                dispatch(new UpdateDevSlot(view, nickname, devSlotIndex, devSlot.stream().map(DevelopmentCard::getId).toList()));
        }
    }

    public void dispatchPrivateState(View view) {
        dispatch(new UpdateLeadersHand(view, nickname, leaders.stream().map(Card::getId).toList()));
    }

    /**
     * Getter of the player's visible nickname.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Checks whether the player has the inkwell, i.e. the player starts first at each round.
     *
     * @return <code>true</code> if this player starts first; <code>false</code> otherwise.
     */
    public boolean hasInkwell() {
        return inkwell;
    }

    /**
     * Getter of the hand of leader cards available to the player.
     *
     * @return the list of leader cards
     */
    public List<LeaderCard> getLeaders() {
        return List.copyOf(leaders);
    }

    public void retainLeaders(View view, List<LeaderCard> leaders) {
        if (!this.leaders.containsAll(leaders))
            throw new RuntimeException(); // TODO: Add more specific exception

        this.leaders.retainAll(leaders);

        dispatch(new UpdateLeadersHand(view, getNickname(), this.leaders.stream().map(Card::getId).toList()));
        dispatch(new UpdateLeadersHandCount(getNickname(), this.leaders.size()));
    }

    /**
     * Action performed when the player discards a leader card.
     *
     * @param game   the game the player is playing in
     * @param leader the leader card to discard
     * @throws ActiveLeaderDiscardException leader is already active
     * @throws IllegalArgumentException     leader isn't owned by the player
     */
    public void discardLeader(View view, Game game, LeaderCard leader) throws IllegalArgumentException, ActiveLeaderDiscardException {
        if (!leaders.contains(leader))
            throw new IllegalArgumentException(
                    String.format("Leader %d cannot be discarded: inexistent leader, max index allowed %d", leaders.indexOf(leader), leaders.size()));
        if (leader.isActive()) throw new ActiveLeaderDiscardException(leaders.indexOf(leader));
        game.onDiscardLeader(this);
        leaders.remove(leader);

        dispatch(new UpdateLeadersHand(view, getNickname(), leaders.stream().map(Card::getId).toList()));
        dispatch(new UpdateLeadersHandCount(getNickname(), leaders.size()));
    }

    /**
     * Advances the faith marker by one on the board's faith track, then proceeds to call a checker for Vatican report
     * tiles "onIncrementFaithPoints".
     *
     * @param game   the game the player is playing in
     * @param points the quantity to be added to the faith points
     */
    public void incrementFaithPoints(Game game, int points) {
        if (points <= 0)
            return;

        faithPoints += points;
        game.updatePtsFromYellowTiles(this, points);
        game.onIncrementFaithPoints(faithPoints);

        dispatch(new UpdateFaithPoints(null, getNickname(), faithPoints, false));
    }

    /**
     * Discards resources.
     *
     * @param game     the game the player is playing in
     * @param quantity the quantity of resources to discard
     */
    public void discardResources(Game game, int quantity) {
        game.onDiscardResources(this, quantity);
    }

    /**
     * Requires access to the Warehouse of the player.
     *
     * @return the player's Warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Requires access to the Strongbox of the player.
     *
     * @return the player's Strongbox
     */
    public Strongbox getStrongbox() {
        return strongbox;
    }

    /**
     * Retrieves the stacks of cards bought and stored in the slots.
     *
     * @return the player's development slots
     */
    public List<Stack<DevelopmentCard>> getDevSlots() {
        return List.copyOf(devSlots);
    }

    /**
     * Returns the position of the player's faith marker.
     *
     * @return the number of the tile reached by the player
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    /**
     * Returns the number of victory points have been scored so far by the player.
     *
     * @return the current player score
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Increments score of the player by a given quantity.
     *
     * @param points the quantity to be added to the score
     */
    public void incrementVictoryPoints(int points) {
        this.victoryPoints += points;
        dispatch(new UpdateVictoryPoints(null, nickname, victoryPoints));
    }

    /**
     * Checks whether the player is connected to the current game.
     *
     * @return <code>true</code> if the player is online; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Changes the state of connection of a player, in case of (dis)connection.
     *
     * @param active the updated state of connection of the player
     */
    public void setActive(boolean active) {
        this.active = active;

        dispatch(new UpdatePlayerStatus(getNickname(), active));
    }

    /**
     * Says whether this player has won the game or not.
     *
     * @return <code>true</code> if this player is winner of the game; <code>false</code> otherwise.
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Declares the winner, by setting the winner flag.
     */
    public void setWinner() {
        // notification is sent one above in the call stack,
        // as the other players' VP have to be sent too
        this.winner = true;
    }

    /**
     * Places a new card on top of a given production slot and consume required resources.
     *
     * @param game          the game the player is playing in
     * @param devSlotIndex  the destination production slot
     * @param devCard       the development card that has just been bought
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws IllegalCardDepositException     blocks the action if the level of the previous top card of the slot is
     *                                         not equal to current level minus 1
     * @throws IllegalResourceTransferException if the player cannot pay for the card
     */
    public void addToDevSlot(Game game, int devSlotIndex, DevelopmentCard devCard,
                             Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalCardDepositException, IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException, IllegalResourceTransferException {
        Stack<DevelopmentCard> devSlot = devSlots.get(devSlotIndex);
        if ((devSlot.isEmpty() && devCard.getLevel() != 1) || (!devSlot.isEmpty() && devSlot.peek().getLevel() != devCard.getLevel() - 1))
            throw new IllegalCardDepositException();

        devCard.takeFromPlayer(game, this, resContainers);

        game.onAddToDevSlot(devSlots.stream().mapToInt(Vector::size).sum());

        devSlot.push(devCard);
        incrementVictoryPoints(devCard.getVictoryPoints());

        dispatch(new UpdateDevSlot(nickname, devSlotIndex, devSlot.stream().map(DevelopmentCard::getId).toList()));
    }

    /**
     * Obtains the base production "recipe" in order to activate a production in it.
     *
     * @return the required base production recipe
     */
    public ResourceTransactionRecipe getBaseProduction() {
        return baseProduction;
    }

    /**
     * Obtains how many resources the player has.
     *
     * @return the total number of resources the player has available
     */
    public int getResourcesCount() {
        return getResContainers().stream().mapToInt(ResourceContainer::getQuantity).sum();
    }

    public PlayerSetup getSetup() {
        return setup;
    }

    public Set<ResourceContainer> getResContainers() {
        Set<ResourceContainer> resContainers = new HashSet<>();
        resContainers.add(strongbox);
        resContainers.addAll(getShelves());
        return resContainers;
    }

    public Set<Shelf> getShelves() {
        Set<Shelf> shelves = new HashSet<>();
        shelves.addAll(warehouse.getShelves());
        shelves.addAll(leaders.stream().map(l -> l.getDepot(false)).flatMap(Optional::stream).toList());
        return shelves;
    }

    public Optional<ResourceContainer> getResourceContainerById(int id) {
        return getResContainers().stream().filter(c -> c.getId() == id).findAny();
    }

    public Optional<Shelf> getShelfById(int id) {
        return getShelves().stream().filter(c -> c.getId() == id).findAny();
    }

    public Optional<LeaderCard> getLeaderById(int id) {
        return leaders.stream().filter(l -> l.getId() == id).findAny();
    }

    public Optional<DevelopmentCard> getDevCardById(int id) {
        return devSlots.stream().map(Stack::peek).filter(d -> d.getId() == id).findAny();
    }

    public Optional<ResourceTransactionRecipe> getProductionById(int id) {
        Optional<ResourceTransactionRecipe> production = baseProduction.getId() == id ? Optional.of(baseProduction) : Optional.empty();
        if (production.isEmpty())
            production = leaders.stream().map(l -> l.getProduction(false)).flatMap(Optional::stream).filter(p -> p.getId() == id).findAny();
        if (production.isEmpty())
            production = devSlots.stream().flatMap(Collection::stream).map(DevelopmentCard::getProduction).filter(p -> p.getId() == id).findAny();
        return production;
    }
}
