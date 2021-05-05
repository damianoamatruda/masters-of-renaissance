package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

import java.util.Map;

public class ReqBuyDevCard implements VCEvent {
    private final String color;
    private final int level;
    private final int slotIndex;
    private final Map<Integer, Map<String, Integer>> resContainers;

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

    public String getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public Map<Integer, Map<String, Integer>> getResContainers() {
        return resContainers;
    }
}
