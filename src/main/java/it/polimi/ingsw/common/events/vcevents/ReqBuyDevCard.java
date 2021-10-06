package it.polimi.ingsw.common.events.vcevents;

import java.util.Map;

/**
 * Client request to buy a development card.
 */
public class ReqBuyDevCard implements VCEvent {
    /** The card's color. */
    private final String color;

    /** The card's level. */
    private final int level;

    /** The index of the development card slot to fit the card into. */
    private final int devSlot;

    /** The details of how the resource payment should be handled. */
    private final Map<Integer, Map<String, Integer>> resContainers;

    /**
     * Class constructor.
     *
     * @param color         the card's color
     * @param level         the card's level
     * @param devSlot       the index of the development card slot to fit the card into
     * @param resContainers the mapping container-resourcetype-quantity that details how the payment should be handled
     */
    public ReqBuyDevCard(String color, int level, int devSlot, Map<Integer, Map<String, Integer>> resContainers) {
        this.color = color;
        this.level = level;
        this.devSlot = devSlot;
        this.resContainers = resContainers;
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
    public int getDevSlot() {
        return devSlot;
    }

    /**
     * @return the mapping container-resourcetype-quantity that details how the payment should be handled
     */
    public Map<Integer, Map<String, Integer>> getResContainers() {
        return resContainers;
    }
}
