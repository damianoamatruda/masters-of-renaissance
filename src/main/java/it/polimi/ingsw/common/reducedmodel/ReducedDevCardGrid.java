package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReducedDevCardGrid {
    /** Number of rows of separate decks that represent different development card levels. */
    protected final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    protected final int colorsCount;

    /** All the cards that are still not bought by any player. */
    protected final Map<String, List<Stack<Integer>>> grid;

    public ReducedDevCardGrid(int levelsCount, int colorsCount, Map<String, List<Stack<Integer>>> grid) {
        this.levelsCount = levelsCount;
        this.colorsCount = colorsCount;
        this.grid = grid;
    }

}
