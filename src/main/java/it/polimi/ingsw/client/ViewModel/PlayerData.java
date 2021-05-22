package it.polimi.ingsw.client.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

public class PlayerData {
    private final int baseProduction;
    /** Card at index 0 is the topmost. */
    private List<List<Integer>> devSlots;
    private int faithPoints;
    private boolean isActive;
    private int leadersCount;
    private List<Integer> leadersHand;
    private final ReducedPlayerSetup setup;
    private final int strongbox;
    private int victoryPoints;
    private final List<Integer> warehouseShelves;

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
     * @param devSlots the devSlots to set
     */
    public void setDevSlot(int slot, int cardID) {
        if (devSlots.size() - 1 < slot)
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
        this.leadersHand = leadersHand;
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
