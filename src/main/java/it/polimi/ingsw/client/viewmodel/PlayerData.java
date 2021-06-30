package it.polimi.ingsw.client.viewmodel;

import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Data relative to a player's objects. */
public class PlayerData {
    private final int baseProduction;

    private final List<List<Integer>> devSlots;

    private int faithPoints;

    private boolean isActive;

    /** When not activated, cards are hidden.
     * Therefore, only their number is stored. */
    private int leadersCount;

    /* For the player owning the leader cards, this list contains
       the IDs of all the leader cards in their hand.
       For every other player, this contains the IDs of the activated (therefore, visible) cards
       of the player that owns them. */
    private List<Integer> leadersHand;

    private final ReducedPlayerSetup setup;

    private final int strongbox;

    private int victoryPoints;

    private final List<Integer> warehouseShelves;

    /**
     * Class constructor.
     *
     * @param baseProduction   the player's base production's ID
     * @param setup            the player's setup details
     * @param strongbox        the player's strongbox's ID
     * @param warehouseShelves the player's warehouse shelves' ID
     */
    public PlayerData(int baseProduction, ReducedPlayerSetup setup, int strongbox, List<Integer> warehouseShelves) {
        this.devSlots = new ArrayList<>();
        this.leadersHand = new ArrayList<>();
        this.baseProduction = baseProduction;
        this.setup = setup;
        this.strongbox = strongbox;
        this.warehouseShelves = warehouseShelves;
    }

    /**
     * @return the baseProduction
     */
    int getBaseProduction() {
        return baseProduction;
    }

    /**
     * @return the devSlots
     */
    List<List<Integer>> getDevSlots() {
        return devSlots;
    }

    /**
     * @param slot    the index of the slot (0-based) to place the card into
     * @param cardIDs the IDs of the cards
     */
    public synchronized void setDevSlot(int slot, List<Integer> cardIDs) {
        while (devSlots.size() - 1 < slot)
            devSlots.add(new ArrayList<>());
        devSlots.set(slot, cardIDs);
    }

    /**
     * @return the faithPoints
     */
    int getFaithPoints() {
        return faithPoints;
    }

    /**
     * @param faithPoints the faithPoints to set
     */
    public synchronized void setFaithPoints(int faithPoints) {
        this.faithPoints = faithPoints;
    }

    /**
     * @return the isActive
     */
    boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the leadersCount
     */
    int getLeadersCount() {
        return leadersCount;
    }

    /**
     * @param leadersCount the leadersCount to set
     */
    public synchronized void setLeadersCount(int leadersCount) {
        this.leadersCount = leadersCount;
    }

    /**
     * @return the leadersHand
     */
    List<Integer> getLeadersHand() {
        return leadersHand;
    }

    /**
     * @param leadersHand the leadersHand to set
     */
    public synchronized void setLeadersHand(List<Integer> leadersHand) {
        if (leadersHand == null)
            return;
        this.leadersHand = new ArrayList<>(leadersHand);
    }

    /**
     * @return the setup
     */
    public synchronized Optional<ReducedPlayerSetup> getSetup() {
        return Optional.ofNullable(setup);
    }

    /**
     * @return the strongbox
     */
    int getStrongbox() {
        return strongbox;
    }

    /**
     * @return the victoryPoints
     */
    int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @param victoryPoints the victoryPoints to set
     */
    public synchronized void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * @return the warehouseShelves
     */
    List<Integer> getWarehouseShelves() {
        return warehouseShelves;
    }
}
