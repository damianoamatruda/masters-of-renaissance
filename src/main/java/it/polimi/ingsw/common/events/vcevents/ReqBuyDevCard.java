package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

import java.util.Map;

/** Client request to buy a development card. */
public class ReqBuyDevCard implements VCEvent {
    /** The card's color. */
    private final String color;
    /** The card's level. */
    private final int level;
    /** The index of the development card slot to fit the card into. */
    private final int slotIndex;
    /** The details of how the resource payment should be handled. */
    private final Map<Integer, Map<String, Integer>> resContainers;

    /**
     * Class constructor.
     * 
     * @param color         the card's color
     * @param level         the card's level
     * @param slotIndex     the index of the development card slot to fit the card into
     * @param resContainers the mapping container-resourcetype-amount
     *                      that details how the payment should be handled
     */
    public ReqBuyDevCard(String color, int level, int slotIndex, Map<Integer, Map<String, Integer>> resContainers) {
        this.color = color;
        this.level = level;
        this.slotIndex = slotIndex;
        this.resContainers = resContainers;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the card's color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the card's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the index of the development card slot to fit the card into
     */
    public int getSlotIndex() {
        return slotIndex;
    }

    /**
     * @return the mapping container-resourcetype-amount
     *         that details how the payment should be handled
     */
    public Map<Integer, Map<String, Integer>> getResContainers() {
        return resContainers;
    }
}
