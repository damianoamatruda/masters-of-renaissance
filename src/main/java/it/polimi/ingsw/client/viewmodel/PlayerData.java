package it.polimi.ingsw.client.viewmodel;

import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

import java.util.ArrayList;
import java.util.List;

/** Data relative to a player's objects. */
public class PlayerData {
    private final int baseProduction;
    /** Card at index 0 is the topmost. */
    private final List<List<Integer>> devSlots;
    private int faithPoints;
    private boolean isActive;
    /** When not activated, cards are hidden.
     * Therefore, only their number is stored. */
    private int leadersCount;
    /* IDs of the activated leader cards.
     * An ID's presence makes the card visible.
     * The card's state also needs to be set to active. */ //Not only the activated, actually
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
        devSlots = new ArrayList<>();
        leadersHand = new ArrayList<>();
        
        this.baseProduction = baseProduction;
        this.setup = setup;
        this.strongbox = strongbox;
        this.warehouseShelves = warehouseShelves;
    }

    /**
     * @return the baseProduction
     */
    public int getBaseProduction() {
        return baseProduction;
    }

    /**
     * @return the devSlots
     */
    public List<List<Integer>> getDevSlots() {
        return devSlots;
    }

    /**
     * @param slot   the index of the slot (0-based) to place the card into
     * @param cardID the ID of the card to place on top (set at position 0)
     */
    public void setDevSlot(int slot, int cardID) {
        while (devSlots.size() - 1 < slot)
            devSlots.add(slot, new ArrayList<>());

        devSlots.get(slot).add(0, cardID);
    }

    /**
     * @return the faithPoints
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    /**
     * @param faithPoints the faithPoints to set
     */
    public void setFaithPoints(int faithPoints) {
        this.faithPoints = faithPoints;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the leadersCount
     */
    public int getLeadersCount() {
        return leadersCount;
    }

    /**
     * @param leadersCount the leadersCount to set
     */
    public void setLeadersCount(int leadersCount) {
        this.leadersCount = leadersCount;
    }

    /**
     * @return the leadersHand
     */
    public List<Integer> getLeadersHand() {
        return leadersHand;
    }

    /**
     * @param leadersHand the leadersHand to set
     */
    public void setLeadersHand(List<Integer> leadersHand) {
        if (leadersHand != null)
            this.leadersHand = new ArrayList<>(leadersHand);
    }

    /**
     * @return the setup
     */
    public ReducedPlayerSetup getSetup() {
        return setup;
    }

    /**
     * @return the strongbox
     */
    public int getStrongbox() {
        return strongbox;
    }

    /**
     * @return the victoryPoints
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @param victoryPoints the victoryPoints to set
     */
    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * @return the warehouseShelves
     */
    public List<Integer> getWarehouseShelves() {
        return warehouseShelves;
    }
}
