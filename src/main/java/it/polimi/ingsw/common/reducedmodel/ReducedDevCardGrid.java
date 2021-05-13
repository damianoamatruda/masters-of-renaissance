package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReducedDevCardGrid {
    /** Number of rows of separate decks that represent different development card levels. */
    private final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    private final int colorsCount;

    /** All the cards that are still not bought by any player. */
    private final Map<String, List<Stack<Integer>>> grid;

    public ReducedDevCardGrid(int levelsCount, int colorsCount, Map<String, List<Stack<Integer>>> grid) {
        this.levelsCount = levelsCount;
        this.colorsCount = colorsCount;
        this.grid = grid;
    }

    /**
     * @return the number of rows of separate decks that represent different development card levels
     */
    public int getLevelsCount() {
        return levelsCount;
    }

    /**
     * @return the number of columns of separate decks that represent different development card colors
     */
    public int getColorsCount() {
        return colorsCount;
    }

    /**
     * @return all the cards that are still not bought by any player
     */
    public Map<String, List<Stack<Integer>>> getGrid() {
        return grid;
    }
}
