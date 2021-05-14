package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.*;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class dedicated to the storage of the player's data and available operations.
 */
public class Player extends ModelObservable {
    /** The player's nickname. */
    private final String nickname;

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

    /** Visible to the view, this indicates whether the player starts first each round. */
    private final boolean inkwell;

    /** The number of leader cards that must be discarded in the early game. */
    private final int chosenLeadersCount;

    /** The number of resources the player can still choose at the beginning. */
    private final int initialResources;

    /** The resources the player cannot choose at the beginning. */
    private final Set<ResourceType> initialExcludedResources;

    /** <code>true</code> if the player has chosen the initial resources; <code>false</code> otherwise. */
    private boolean hasChosenResources;

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
     * @param chosenLeadersCount       number of leader cards that must be discarded in the early game
     * @param initialResources         number of resources the player can choose at the beginning
     * @param initialFaith             initial faith points
     * @param initialExcludedResources resources the player cannot choose at the beginning
     */
    public Player(String nickname, boolean inkwell, List<LeaderCard> leaders, Warehouse warehouse, Strongbox strongbox,
                  ResourceTransactionRecipe baseProduction, int devSlotsCount, int chosenLeadersCount, int initialResources,
                  int initialFaith, Set<ResourceType> initialExcludedResources) {
        this.nickname = nickname;
        this.inkwell = inkwell;
        this.leaders = new ArrayList<>(leaders);
        this.warehouse = warehouse;
        this.strongbox = strongbox;

        this.devSlots = new ArrayList<>();
        for (int i = 0; i < devSlotsCount; i++)
            this.devSlots.add(new Stack<>());

        this.baseProduction = baseProduction;
        this.chosenLeadersCount = chosenLeadersCount;
        this.initialResources = initialResources;
        this.initialExcludedResources = Set.copyOf(initialExcludedResources);
        this.hasChosenResources = initialResources == 0;
        this.faithPoints = initialFaith; /* This does not trigger game.onIncrementFaithPoints(initialFaith); */
        this.victoryPoints = 0;
        this.active = true;
        this.winner = false;
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
     * Getter of the hand of leader cards available to the player.
     *
     * @return the list of leader cards
     */
    public List<LeaderCard> getLeaders() {
        return List.copyOf(leaders);
    }

    /**
     * Chooses leaders from the hand of the player.
     *
     * @param chosenLeaders the leader cards to choose
     * @throws CannotChooseException if the leader cards have already been chosen
     */
    public void chooseLeaders(List<LeaderCard> chosenLeaders) throws CannotChooseException {
        if (leaders.size() == chosenLeadersCount)
            throw new CannotChooseException("Cannot choose starting leaders again, choice already made."); // TODO what happens if server parameters make this default?

        if (chosenLeaders.size() != chosenLeadersCount)
            throw new CannotChooseException(
                String.format("Not enough leader cards chosen: %d chosen, %d required.", chosenLeaders.size(), chosenLeadersCount));
        
        if (!leaders.containsAll(chosenLeaders))
            throw new CannotChooseException(
                String.format("Cannot choose leader %d: not in your hand.",
                    leaders.stream()
                        .map(l -> l.getId())
                        .filter(id -> !chosenLeaders.stream().map(cl -> cl.getId()).toList().contains(id))
                        .findAny().orElse(-1)));

        leaders.retainAll(chosenLeaders);

        notifyBroadcast(new UpdateChooseLeaders(getNickname()));
    }

    /**
     * Chooses the initial resources to be given to the player.
     *
     * @param game    the game the player is playing in
     * @param shelves the destination shelves
     * @throws CannotChooseException                         all the allowed initial resources have already been chosen
     * @throws IllegalResourceTransactionActivationException invalid container
     */
    public void chooseResources(Game game, Map<Shelf, Map<ResourceType, Integer>> shelves) throws CannotChooseException, IllegalResourceTransactionActivationException {
        if (hasChosenResources)
            throw new CannotChooseException("Cannot choose starting resources again, choice already made.");

        Map<ResourceType, Integer> chosenResources = shelves.values().stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));

        ResourceTransactionRequest transactionRequest;

        try {
            transactionRequest = new ResourceTransactionRequest(
                    new ResourceTransactionRecipe(Map.of(), 0, Set.of(), Map.of(), initialResources, initialExcludedResources, false),
                    Map.of(), chosenResources, Map.of(), Map.copyOf(shelves));
        } catch (IllegalResourceTransactionReplacementsException | IllegalResourceTransactionContainersException e) {
            throw new IllegalResourceTransactionActivationException(e); // TODO: Add more specific exception
        }

        new ResourceTransaction(List.of(transactionRequest)).activate(game, this);

        hasChosenResources = true;
    }

    /**
     * @return the number of leaders to be chosen during the setup.
     */
    public int getChosenLeadersCount() {
        return chosenLeadersCount;
    }

    /**
     * Returns whether the player has chosen the leaders.
     *
     * @return <code>true</code> if the player has chosen the leaders; <code>false</code> otherwise.
     */
    public boolean hasChosenLeaders() {
        return leaders.size() == chosenLeadersCount;
    }

    /**
     * Returns whether the player has chosen the initial resources.
     *
     * @return <code>true</code> if the player has chosen the initial resources; <code>false</code> otherwise.
     */
    public boolean hasChosenResources() {
        return hasChosenResources;
    }

    /**
     * Returns whether the player has done the setup.
     *
     * @return <code>true</code> if the player has done the setup; <code>false</code> otherwise.
     */
    public boolean hasDoneSetup() {
        return hasChosenLeaders() && hasChosenResources();
    }

    /**
     * Action performed when the player discards a leader card.
     *
     * @param game   the game the player is playing in
     * @param leader the leader card to discard
     * @throws ActiveLeaderDiscardException leader is already active
     */
    public void discardLeader(Game game, LeaderCard leader) throws IndexOutOfBoundsException, ActiveLeaderDiscardException {
        if (!leaders.contains(leader))
            throw new IndexOutOfBoundsException(
                    String.format("Leader %d cannot be discarded: inexistent leader, max index allowed %d", leaders.indexOf(leader), leaders.size()));
        if (leader.isActive()) throw new ActiveLeaderDiscardException(leaders.indexOf(leader));
        game.onDiscardLeader(this);
        leaders.remove(leader);

        notifyBroadcast(new UpdateLeader(leader.getId(), false, true));
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

        notifyBroadcast(new UpdateFaithPoints(getNickname(), faithPoints, false));
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
     * Checks whether the player has the inkwell, i.e. the player starts first at each round.
     *
     * @return <code>true</code> if this player starts first; <code>false</code> otherwise.
     */
    public boolean hasInkwell() {
        return inkwell;
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
        notifyBroadcast(new UpdateVictoryPoints(nickname, victoryPoints));
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

        notifyBroadcast(new UpdatePlayerStatus(getNickname(), active));
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
     * @throws CardRequirementsNotMetException requirements for card purchase are not satisfied
     */
    public void addToDevSlot(Game game, int devSlotIndex, DevelopmentCard devCard,
                             Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws CardRequirementsNotMetException, IllegalCardDepositException {
        Stack<DevelopmentCard> slot = devSlots.get(devSlotIndex);
        if ((slot.isEmpty() && devCard.getLevel() != 1) || (!slot.isEmpty() && slot.peek().getLevel() != devCard.getLevel() - 1))
            throw new IllegalCardDepositException(devCard, slot, devSlotIndex);

        devCard.takeFromPlayer(game, this, resContainers);

        game.onAddToDevSlot(this.devSlots.stream().mapToInt(Vector::size).sum());

        slot.push(devCard);

        incrementVictoryPoints(devCard.getVictoryPoints());
        notifyBroadcast(new UpdateDevCardSlot(devCard.getId(), devSlotIndex));
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
        int quantity = 0;
        quantity += strongbox.getQuantity();
        quantity += warehouse.getShelves().stream()
                .mapToInt(Shelf::getQuantity)
                .sum();
        return quantity;
    }

    public List<String> getInitialExcludedResources() {
        return initialExcludedResources.stream().map(ResourceType::getName).toList();
    }

    public int getInitialResources() {
        return initialResources;
    }

    public Optional<Strongbox> getStrongboxById(int id) {
        return strongbox.getId() == id ? Optional.of(strongbox) : Optional.empty();
    }

    public Optional<Shelf> getShelfById(int id) {
        Optional<Shelf> shelf = Optional.ofNullable(warehouse.getShelves().stream().filter(s -> s.getId() == id).findAny().orElse(null));
        if (shelf.isEmpty())
            shelf = Optional.ofNullable(leaders.stream().map(LeaderCard::getDepot).filter(Optional::isPresent).map(Optional::get).filter(d -> d.getId() == id).findAny().orElse(null));
        return shelf;
    }

    public Optional<ResourceContainer> getResourceContainerById(int id) {
        Optional<ResourceContainer> resContainer = Optional.ofNullable(getStrongboxById(id).orElse(null));
        if (resContainer.isEmpty())
            resContainer = Optional.ofNullable(getShelfById(id).orElse(null));
        return resContainer;
    }

    public Optional<LeaderCard> getLeaderById(int id) {
        return leaders.stream().filter(l -> l.getId() == id).findAny();
    }

    public Optional<DevelopmentCard> getDevCardById(int id) {
        return devSlots.stream().flatMap(Collection::stream).filter(d -> d.getId() == id).findAny();
    }

    public Optional<ResourceTransactionRecipe> getProductionById(int id) {
        Optional<ResourceTransactionRecipe> production = baseProduction.getId() == id ? Optional.of(baseProduction) : Optional.empty();
        if (production.isEmpty())
            production = leaders.stream().map(LeaderCard::getProduction).filter(Optional::isPresent).map(Optional::get).filter(p -> p.getId() == id).findAny();
        if (production.isEmpty())
            production = devSlots.stream().flatMap(Collection::stream).map(DevelopmentCard::getProduction).filter(p -> p.getId() == id).findAny();
        return production;
    }

    @Override
    public String toString() {
        return nickname;
    }
}
