package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

import java.util.Map;

public class ReqTakeFromMarket implements VCEvent {
    private final boolean isRow;
    private final int index;
    private final Map<String, Integer> replacements;
    private final Map<Integer, Map<String, Integer>> shelves;

    public ReqTakeFromMarket(boolean isRow, int index, Map<String, Integer> replacements, Map<Integer, Map<String, Integer>> shelves) {
        this.isRow = isRow;
        this.index = index;
        this.replacements = replacements;
        this.shelves = shelves;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public boolean isRow() {
        return isRow;
    }

    public int getIndex() {
        return index;
    }

    public Map<String, Integer> getReplacements() {
        return replacements;
    }

    public Map<Integer, Map<String, Integer>> getShelves() {
        return shelves;
    }
}
