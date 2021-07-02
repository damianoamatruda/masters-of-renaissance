package it.polimi.ingsw.common.reducedmodel;

import java.util.ArrayList;
import java.util.List;

public class ReducedPlayer {
    private final String nickname;

    private final int baseProduction;

    private final List<Integer> warehouseShelves;

    private final int strongbox;

    private final ReducedPlayerSetup setup;

    /* For the player owning the leader cards, this list contains
       the IDs of all the leader cards in their hand.
       For every other player, this contains the IDs of the activated (therefore, visible) cards
       of the player that owns them. */
    private final List<Integer> leadersHand;

    /**
     * When not activated, cards are hidden. Therefore, only their number is stored.
     */
    private final int leadersHandCount;

    private final List<List<Integer>> devSlots;

    private final int faithPoints;

    private final int victoryPoints;

    private final boolean active;

    /**
     * Class constructor.
     *
     * @param nickname
     * @param baseProduction   the player's base production's ID
     * @param warehouseShelves the player's warehouse shelves' ID
     * @param strongbox        the player's strongbox's ID
     * @param setup            the player's setup details
     * @param leadersHand      the player's leader cards for the cards' owner, the active leader cards for non-owner players
     * @param leadersHandCount the number of leader cards owned by the player
     * @param devSlots         the player's development card slots
     * @param faithPoints      the player's faith points
     * @param victoryPoints    the player's victory points
     * @param active           the player's status
     */
    public ReducedPlayer(String nickname, int baseProduction, List<Integer> warehouseShelves, int strongbox, ReducedPlayerSetup setup, List<Integer> leadersHand, int leadersHandCount, List<List<Integer>> devSlots, int faithPoints, int victoryPoints, boolean active) {
        this.nickname = nickname;
        this.baseProduction = baseProduction;
        this.warehouseShelves = warehouseShelves;
        this.strongbox = strongbox;
        this.setup = setup;
        this.leadersHand = leadersHand;
        this.leadersHandCount = leadersHandCount;
        this.devSlots = devSlots;
        this.faithPoints = faithPoints;
        this.victoryPoints = victoryPoints;
        this.active = active;
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the baseProduction
     */
    public int getBaseProduction() {
        return baseProduction;
    }

    /**
     * @return the warehouseShelves
     */
    public List<Integer> getWarehouseShelves() {
        return warehouseShelves;
    }

    /**
     * @return the strongbox
     */
    public int getStrongbox() {
        return strongbox;
    }

    /**
     * @return the setup
     */
    public ReducedPlayerSetup getSetup() {
        return setup;
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
    public ReducedPlayer setLeadersHand(List<Integer> leadersHand) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
    }

    /**
     * @return the leadersHandCount
     */
    public int getLeadersHandCount() {
        return leadersHandCount;
    }

    /**
     * @param leadersHandCount the leadersHandCount to set
     */
    public ReducedPlayer setLeadersHandCount(int leadersHandCount) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
    }

    /**
     * @return the devSlots
     */
    public List<List<Integer>> getDevSlots() {
        return devSlots;
    }

    public ReducedPlayer setDevSlots(List<List<Integer>> devSlots) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
    }

    public ReducedPlayer setDevSlot(int devSlotIndex, List<Integer> devSlot) {
        List<List<Integer>> devSlots = new ArrayList<>(this.devSlots);
        while (devSlots.size() - 1 < devSlotIndex)
            devSlots.add(new ArrayList<>());
        devSlots.set(devSlotIndex, devSlot);
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
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
    public ReducedPlayer setFaithPoints(int faithPoints) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
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
    public ReducedPlayer setVictoryPoints(int victoryPoints) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public ReducedPlayer setActive(boolean active) {
        return new ReducedPlayer(nickname, baseProduction, warehouseShelves, strongbox, setup, leadersHand, leadersHandCount, devSlots, faithPoints, victoryPoints, active);
    }
}
