package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.util.Map;

/** Client request to take resources from the market. */
public class ReqTakeFromMarket implements VCEvent {
    /** Whether the index refers to a row or a column. */
    private final boolean isRow;
    /** The index of the row/column to take resources from. */
    private final int index;
    /** The mapping detailing how to handle the replacement of the blank resources. */
    private final Map<String, Integer> replacements;
    /** The mapping detailing how to handle the positioning of the resources in the shelves. */
    private final Map<Integer, Map<String, Integer>> shelves;

    /**
     * Class constructor.
     * 
     * @param isRow        whether the index refers to a row or a column
     * @param index        the index of the row/column to take resources from
     * @param replacements the mapping detailing how to handle the replacement of the blank resources
     * @param shelves      the mapping detailing how to handle the positioning of the resources in the shelves
     */
    public ReqTakeFromMarket(boolean isRow, int index, Map<String, Integer> replacements, Map<Integer, Map<String, Integer>> shelves) {
        this.isRow = isRow;
        this.index = index;
        this.replacements = replacements;
        this.shelves = shelves;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return whether the index refers to a row or a column
     */
    public boolean isRow() {
        return isRow;
    }

    /**
     * @return the index of the row/column to take resources from
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the mapping detailing how to handle the replacement of the blank resources
     */
    public Map<String, Integer> getReplacements() {
        return replacements;
    }

    /**
     * @return the mapping detailing how to handle the positioning of the resources in the shelves
     */
    public Map<Integer, Map<String, Integer>> getShelves() {
        return shelves;
    }
}
