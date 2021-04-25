package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cardrequirements.RequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.*;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.*;

/**
 * Class dedicated to the storage of the player's data and available operations.
 */
public class Player {
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
    private final Production baseProduction;

    /** Visible to the view, this indicates whether the player starts first each round. */
    private final boolean inkwell;

    /** The number of leader cards that must be discarded in the early game. */
    private final int chosenLeadersCount;

    /** The number of (storable) resources the player can still choose at the beginning */
    private final int initialResources;

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
     * @param nickname           the player's nickname to be seen by all the players
     * @param inkwell            received only by the first player
     * @param leaders            the leader cards in the player's hand
     * @param warehouse          the player's warehouse
     * @param strongbox          the player's strongbox
     * @param baseProduction     the player's base production
     * @param devSlotsCount      number of possible production slots that can be occupied by development cards
     * @param chosenLeadersCount number of leader cards that must be discarded in the early game
     * @param initialResources   number of storable resources the player can choose at the beginning
     * @param initialFaith       initial faith points
     */
    public Player(String nickname, boolean inkwell, List<LeaderCard> leaders, Warehouse warehouse, Strongbox strongbox,
                  Production baseProduction, int devSlotsCount, int chosenLeadersCount, int initialResources,
                  int initialFaith) {
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
        this.hasChosenResources = initialResources == 0;
        this.faithPoints = initialFaith;
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
     */
    public void chooseLeaders(List<LeaderCard> chosenLeaders) throws CannotChooseException {
        if (chosenLeaders.size() != chosenLeadersCount || !leaders.containsAll(chosenLeaders))
            throw new CannotChooseException();
        leaders.retainAll(chosenLeaders);
    }

    /**
     * Chooses the initial resources to be given to the player.
     *
     * @param game            the game the player is playing in
     * @param chosenResources the chosen resources
     * @param shelves         the destination shelves
     * @throws CannotChooseException            all the allowed initial resources have already been chosen
     * @throws InvalidChoiceException           the resource cannot be given
     * @throws IllegalResourceTransferException invalid container
     */
    public void chooseResources(Game game, Map<ResourceType, Integer> chosenResources, Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws CannotChooseException, InvalidChoiceException, IllegalResourceTransferException {
        // TODO: Exclude resource type "Faith" from chosenResources
        // TODO: Make sure that shelves are of type Shelf

        if (hasChosenResources)
            throw new CannotChooseException();

        try {
            new ProductionGroup(List.of(
                    new ProductionGroup.ProductionRequest(
                            new Production(Map.of(), 0, Set.of(), Map.of(), initialResources, Set.of(), false),
                            Map.of(), chosenResources, Map.of(), shelves)
            )).activate(game, this);
        } catch (IllegalProductionActivationException e) {
            throw new InvalidChoiceException();
        }

        hasChosenResources = true;
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
     * Action performed when the player discards a leader card.
     *
     * @param game   the game the player is playing in
     * @param leader the leader card to discard
     * @throws AlreadyActiveException leader is already active
     */
    public void discardLeader(Game game, LeaderCard leader) throws IllegalActivationException, AlreadyActiveException {
        if (!leaders.contains(leader))
            throw new IllegalActivationException("The leader card cannot be discarded");
        if (leader.isActive()) throw new AlreadyActiveException();
        game.onDiscardLeader(this);
        leaders.remove(leader);
    }

    /**
     * Advances the faith marker by one on the board's faith track, then proceeds to call a checker for Vatican report
     * tiles "onIncrement".
     *
     * @param game the game the player is playing in
     */
    public void incrementFaithPoints(Game game) {
        faithPoints += 1;
        game.onIncrement(faithPoints);
    }

    /**
     * Discards a resource.
     *
     * @param game     the game the player is playing in
     * @param resource the type of the resource to discard
     */
    public void discardResource(Game game, ResourceType resource) {
        game.onDiscardResource(this);
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
     *
     * @param winner <code>true</code> if this player is to be declared as winner; <code>false</code> otherwise.
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * Places a new card on top of a given production slot and consume required resources.
     *
     * @param game          the game the player is playing in
     * @param index         the destination production slot
     * @param devCard       the development card that has just been bought
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws IllegalCardDepositException blocks the action if the level of the previous top card of the slot is not
     *                                     equal to current level minus 1
     * @throws RequirementsNotMetException requirements for card purchase are not satisfied
     */
    public void addToDevSlot(Game game, int index, DevelopmentCard devCard,
                             Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws RequirementsNotMetException, IllegalCardDepositException {
        Stack<DevelopmentCard> slot = devSlots.get(index);
        if ((slot.isEmpty() && devCard.getLevel() != 1) || (!slot.isEmpty() && slot.peek().getLevel() != devCard.getLevel() - 1))
            throw new IllegalCardDepositException();

        devCard.takeFromPlayer(game, this, resContainers);

        game.onAddToDevSlot(this.devSlots.stream().mapToInt(Vector::size).sum());

        slot.push(devCard);
    }

    /**
     * Obtains the base production "recipe" in order to activate a production in it.
     *
     * @return the required base production recipe
     */
    public Production getBaseProduction() {
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

    /**
     * Sums points earned from all development cards collected and from activated leader cards.
     */
    public void sumCardsVictoryPoints() {
        int toSum = devSlots.stream()
                .mapToInt(slot -> slot.stream()
                        .mapToInt(Card::getVictoryPoints)
                        .sum())
                .sum();

        toSum += leaders.stream()
                .filter(LeaderCard::isActive)
                .mapToInt(Card::getVictoryPoints)
                .sum();

        this.victoryPoints += toSum;
    }

    @Override
    public String toString() {
        return nickname;
    }
}
