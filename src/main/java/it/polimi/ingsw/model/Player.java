package it.polimi.ingsw.model;

import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;

import java.util.*;

/**
 * Class dedicated to the storage of the player's data and available operations.
 */
public class Player {
    /** Number of development cards the player can have, before triggering the end of the game. */
    private final int maxObtainableDevCards;

    /** The player's nickname. */
    private String nickname;

    /** The hand of leader cards available to the player. */
    private List<LeaderCard> leaders;

    /** The player's warehouse, standard container of buyable resources. */
    private Warehouse warehouse;

    /** The player's strongbox, where all the production output goes. */
    private Strongbox strongbox;

    /** The collection of cards in each production slot. */
    private List<Stack<DevelopmentCard>> devSlots;

    /** The base production "recipe". */
    private Production baseProduction;

    /** Visible to the view, this indicates whether the player starts first each turn. */
    private boolean inkwell;

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
     * @param nickname              the player's nickname to be seen by all the players
     * @param leaders               the 4 initially assigned leader cards
     * @param inkwell               received only by the first player
     * @param warehouseShelvesCount number of basic shelves inside of the player's warehouse
     * @param devSlotsCount         number of possible production slots that can be occupied by development cards
     * @param maxObtainableDevCards number of development cards the player can have, before triggering the end of the game
     */
    public Player(String nickname,
                  List<LeaderCard> leaders,
                  boolean inkwell,
                  int warehouseShelvesCount,
                  int devSlotsCount,
                  int maxObtainableDevCards){
        this.nickname=nickname;
        this.leaders=leaders;
        this.warehouse = new Warehouse(warehouseShelvesCount);
        this.strongbox = new Strongbox();
        this.inkwell=inkwell;
        faithPoints=0;
        victoryPoints=0;
        active=true;
        winner=false;
        devSlots = new ArrayList<>();

        for (int i = 0; i < devSlotsCount; i++)
            devSlots.add(new Stack<>());

        this.maxObtainableDevCards = maxObtainableDevCards;
    }

    /**
     * Getter of the number of production slots available.
     *
     * @return  the number of slots
     */
    public int getDevSlotsCount(){
        return devSlots.size();
    }

    /**
     * Getter of the player's visible nickname.
     *
     * @return  the player's nickname
     */
    public String getNickname(){
        return nickname;
    }

    /**
     * Getter of the remaining amount of leader cards that are, or can be, activated.
     *
     * @return  the left amount of leader cards that are accessible to the player
     */
    public int getLeadersCount(){
        return leaders.size();
    }

    /** Getter of the leader card at the corresponding index.
     *
     * @param index the index that points the card to be retrieved
     * @return      the required leader card
     */
    public LeaderCard getLeader(int index){
        return leaders.get(index);
    }

    /**
     * Action performed when the player discards a leader card. The player receives one faith point.
     *
     * @param game          the game the player is playing in
     * @param index         the index of the card to be discarded
     * @throws Exception    leader is already active
     */
    public void discardLeader(Game game, int index) throws Exception {
        LeaderCard toBeDiscarded = getLeader(index);
        if(toBeDiscarded.isActive()) throw new Exception();
        toBeDiscarded.onDiscarded(game, this);
        leaders.remove(index);
    }

    /**
     * Advances the faith marker by one on the board's faith track, then proceeds to call a checker for Vatican report
     * tiles "onIncrement".
     *
     * @param game  the game the player is playing in
     */
    public void incrementFaithPoints(Game game) {
        faithPoints += 1;
        game.onIncrement(faithPoints);
    }

    /**
     * Requires access to the Warehouse of the player.
     *
     * @return  the player's Warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Requires access to the Strongbox of the player.
     *
     * @return  the player's Strongbox
     */
    public Strongbox getStrongbox() {
        return strongbox;
    }

    /**
     * Retrieves the stack of cards bought and stored in a given slot.
     *
     * @param index the number of slot to be accessed
     * @return      the cards located in the required slot
     */
    public Stack<DevelopmentCard> getDevSlot(int index){
        return devSlots.get(index);
    }

    /**
     * Checks whether the player has the inkwell, i.e. the player starts first at each turn.
     *
     * @return  <code>true</code> if this player starts first; <code>false</code> otherwise.
     */
    public boolean hasInkwell(){
        return inkwell;
    }

    /**
     * Gets the position of the player's faith marker.
     *
     * @return  the number of the tile reached by the player
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    /**
     * Says how many victory points have been scored so far by the player.
     *
     * @return  the current player score
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Increments score of the player by a given quantity.
     *
     * @param points    the quantity to be added to the score
     */
    public void incrementVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    /**
     * Checks whether the player is connected to the current game.
     *
     * @return  <code>true</code> if the player is online; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Changes the state of connection of a player, in case of (dis)connection.
     *
     * @param   active the updated state of connection of the player
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Says whether this player has won the game or not.
     *
     * @return  <code>true</code> if this player is winner of the game; <code>false</code> otherwise.
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Declares the winner, by setting the winner flag.
     *
     * @param winner    <code>true</code> if this player is to be declared as winner; <code>false</code> otherwise.
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
     * @throws Exception    blocks the action if the level of the previous top card of the slot is not equal to current level minus 1
     * @throws Exception    error during the actual payment
     * @return              true if Player has reached number of development cards required to end the game
     */
    public boolean addToDevSlot(Game game, int index, DevelopmentCard devCard,
                                Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws Exception {
        Stack<DevelopmentCard> slot = devSlots.get(index);
        if((slot.isEmpty() && devCard.getLevel()!=1) || (!slot.isEmpty() && slot.peek().getLevel() != devCard.getLevel()-1)) throw new Exception();

        devCard.getCost().take(game, this, resContainers);

        slot.push(devCard);

        return devSlots.stream()
                .mapToInt(stack -> stack.size())
                .sum() == maxObtainableDevCards;
    }

    /**
     * Obtains the base production "recipe" in order to activate a production in it.
     *
     * @return  the required base production recipe
     */
    public Production getBaseProduction(){
        return baseProduction;
    }

    /**
     * Obtains how many resources the player has.
     *
     * @return  the total number of resources the player has available
     */
    public int getNumOfResources(){
        int quantity = 0;
        quantity += strongbox.getQuantity();
        quantity += warehouse.getShelves().stream()
                .mapToInt(shelf -> shelf.getQuantity())
                .sum();
        return quantity;
    }

    /**
     * Sums points earned from all development cards collected and from activated leader cards.
     */
    public void sumCardsVictoryPoints(){
        int toSum = devSlots.stream()
                .mapToInt(slot -> slot.stream()
                        .mapToInt(card -> card.getVictoryPoints())
                        .sum())
                .sum();

        toSum += leaders.stream()
                .filter(card -> card.isActive())
                .mapToInt(card -> card.getVictoryPoints())
                .sum();

        this.victoryPoints += toSum;
    }

    @Override
    public String toString() {
        return nickname;
    }
}