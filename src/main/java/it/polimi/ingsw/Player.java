package it.polimi.ingsw;

import it.polimi.ingsw.strongboxes.Shelf;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.List;
import java.util.Stack;

/**
 * Class dedicated to the storage of the player's data and available operations
 */

public class Player {
    /** Number of possible production slots that can be occupied by development cards */
    private static final int DEV_SLOTS_COUNT=3;

    /** Number of the last reachable faith track tile */
    private static final int MAX_FAITH_POINTS_COUNT=24;

    /** Number of basic shelves inside of the player's warehouse */
    private static final int WAREHOUSE_SHELVES_COUNT=3;

    /** The current game instance in which the player is playing */
    private Game game;

    /** The player's nickname */
    private String nickname;

    /** The hand of leader cards available to the player */
    private List<LeaderCard> leaders;

    /** The player's warehouse, standard container of buyable resources */
    private List<Shelf> warehouse;

    /** The player's strongbox, where all the production output goes */
    private Strongbox strongbox;

    /** The collection of cards in each production slot */
    private List<Stack<DevelopmentCard>> devSlots;

    /** The base production "recipe" */
    private Production baseProduction;

    /** Visible to the view, this indicates whether the player starts first each turn */
    private boolean inkwell;

    /** The player's faith track marker */
    private int faithPoints;

    /** The player's score */
    private int victoryPoints;

    /** The player's state of connection to the game */
    private boolean active;

    /** The flag to be set when a winner has been decided */
    private boolean winner;

    /** Initializes player's attributes
     * @param game      the game to which the player is connected
     * @param nickname  the player's nickname to be seen by all the players
     * @param leaders   the 4 initially assigned leader cards
     * @param inkwell   received only by the first player
     */
    public Player(Game game, String nickname, List<LeaderCard> leaders, boolean inkwell){
        this.game=game;
        this.nickname=nickname;
        this.leaders=leaders;
        this.inkwell=inkwell;
        faithPoints=0;
        victoryPoints=0;
        active=true;
        winner=false;
    }

    /** Getter of the number of production slots available
     * @return the number of slots */
    public static int getDevSlotsCount(){
        return DEV_SLOTS_COUNT;
    }

    /** Getter of the number of the last reachable tile
     * @return the last reachable tile */
    public static int getMaxFaithPointsCount(){
        return MAX_FAITH_POINTS_COUNT;
    }

    /** Getter of the number of basic shelves of which the warehouse is composed
     * @return the number of basic shelves (leader depots excluded) */
    public static int getShelvesCount(){
        return WAREHOUSE_SHELVES_COUNT;
    }

    /** Getter of the player's visible nickname
     * @return the player's nickname */
    public String getNickname(){
        return nickname;
    }

    /** Getter of the remaining amount of leader cards that are, or can be, activated
     * @return the left amount of leader cards that are accessible to the player */
    public int getLeadersCount(){
        return leaders.size();
    }

    /** Getter of the leader card at the corresponding index
     * @param index the index that points the card to be retrieved
     * @return      the required leader card */
    public LeaderCard getLeader(int index){
        return leaders.get(index);
    }

    /**
     * Action performed when the player discards a leader card. The player receives one faith point
     * @param index the index of the card to be discarded
     */
    public void discardLeader(int index){
        leaders.remove(index);
        for(Player p : game.getPlayers()){
            if(!p.equals(this))
                p.incrementFaithPoints();
        }
    }

    /**
     * Advances the faith marker by one on the board's faith track.
     * Then proceeds to call a checker for Vatican report tiles "onIncrement"
     */
    public void incrementFaithPoints() {
        faithPoints += 1;
        game.onIncrement(faithPoints);
    }

    /**
     * Retrieves the required shelf in order to perform further actions on it
     * @param index the number of shelf to be retrieved
     * @return      the required shelf
     */
    public Shelf getShelf(int index){
        return warehouse.get(index);
    }

    /**
     * Requires access to the Strongbox of the player
     * @return the player's Strongbox
     */
    public Strongbox getStrongbox() {
        return strongbox;
    }

    /**
     * Retrieves the stack of cards bought and stored in a given slot
     * @param index the number of slot to be accessed
     * @return      the cards located in the required slot
     */
    public Stack<DevelopmentCard> getDevSlot(int index){
        return devSlots.get(index);
    }

    /**
     * Checks whether the player has the inkwell, i.e. the player starts first at each turn
     * @return  true if this player starts first
     */
    public boolean hasInkwell(){
        return inkwell;
    }

    /**
     * Gets the position of the player's faith marker
     * @return  The number of tile reached by the player
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    /**
     * Says how many victory points have been scored so far by the player
     * @return  current player score
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Increments score of the player by a given quantity
     * @param points    the quantity to be added to the score
     */
    public void incrementVictoryPoints(int points) {
        this.victoryPoints += victoryPoints;
    }

    /**
     * Checks whether the player is connected to the current game
     * @return  true if player is online
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Changes the state of connection of a player, in case of (dis)connection
     * @param   active the updated state of connection of the player
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Says whether this player has won the game or not
     * @return  true if this player is winner of the game
     */
    public boolean isWinner() {
        return winner;
    }

    /** Declares the winner, by setting the winner flag
     * @param winner    true if player is to be declared as winner
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /** Decides whether a given leader card can still be discarded
     * @param index number of the leader card to be checked
     * @return      true if the leader card can be discarded during the current turn
     */
    private boolean canDiscardLeader(int index){
        return !getLeader(index).isActive();
    }

    /** Decides whether an available development card can be deposited on top of a given production slot
     * @param index     the number of slot to be checked
     * @param devCard   the development card that the user wishes to buy
     * @return          true if the current top card of the slot is of the previous level, thus the action can be done
     */
    private boolean canAddToDevSlot(int index, DevelopmentCard devCard){
        return getDevSlot(index).peek().getLevel() < 3;
    }

    /**
     * Places a new card on top of a given production slot
     * @param index         the destination production slot
     * @param devCard       the development card that has just been bought
     * @throws Exception    blocks the action if the level of the previous top card of the slot is not equal to current level minus 1
     */
    public void addToDevSlot(int index, DevelopmentCard devCard) throws Exception {
        Stack<DevelopmentCard> slot = devSlots.get(index);
        if(slot.peek().getLevel()!=devCard.getLevel()-1) throw new Exception();
        slot.push(devCard);
    }

    /**
     * Obtains the base production "recipe" in order to activate a production in it
     * @return the required base production recipe
     */
    public Production getBaseProduction(){
        return baseProduction;
    }
}
