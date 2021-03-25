package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a special container of resources that can be taken from the player.
 * It consists of a grid of resources together with a slide containing a single resource.
 */
public class Market {
    /** The number of rows in the grid. */
    private static final int ROWS_COUNT = 3;

    /** The number of columns in the grid. */
    private static final int COLS_COUNT = 4;

    /** The number of blue resources. */
    private static final int BLUE_RES_COUNT = 2;

    /** The number of green resources. */
    private static final int GREEN_RES_COUNT = 2;

    /** The number of purple resources. */
    private static final int PURPLE_RES_COUNT = 2;

    /** The number of red resources. */
    private static final int RED_RES_COUNT = 1;

    /** The number of white resources. */
    private static final int WHITE_RES_COUNT = 4;

    /** The number of yellow resources. */
    private static final int YELLOW_RES_COUNT = 2;

    /** The resources in the grid. */
    private final List<List<ResourceType>> gridResources;

    /** The resource in the slide. */
    private ResourceType slideResource;

    /**
     * Initializes the market by placing the resources randomly in the grid and in the slide.
     */
    public Market() {
        gridResources = new ArrayList<>();
        slideResource = new ResourceType();
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return  the number of rows
     */
    public static int getRowsCount() {
        return ROWS_COUNT;
    }

    /**
     * Returns the number of columns in the grid.
     *
     * @return  the number of columns
     */
    public static int getColsCount() {
        return COLS_COUNT;
    }

    /**
     * Returns the types of resources in the grid.
     *
     * @return  the types of resources
     */
    public List<List<ResourceType>> getGridResources() {
        return gridResources;
    }

    /**
     * Returns the type of the resource in the slide.
     *
     * @return  the type of the resource
     */
    public ResourceType getSlideResource() {
        return slideResource;
    }

    /**
     * Takes a chosen row or column of resources from the grid. Each resource is added in a given shelf, if possible,
     * and triggers an action on the player, if applicable.
     *
     * After taking the resources, the chosen row or column is shifted one place from respectively the right or bottom,
     * the resource in the slide takes the uncovered place in the grid and the leftover resource goes into the slide.
     *
     * @param player    the player on which to trigger the action of the resource, if applicable
     * @param isRow     true if a row is selected, false if a column is selected
     * @param index     index of the selected row or column
     * @param zeros     a map of the chosen resources to take, if choices are applicable
     * @param shelves   a map of the shelves where to add the taken resources, if possible
     */
    public void takeResources(Player player, boolean isRow, int index, Map<ResourceType, Integer> zeros,
                              Map<ResourceType, Map<Shelf, Integer>> shelves) {

    }

    /**
     * Checks if it is possible to take resources with the given parameters.
     *
     * @param player    the player on which to trigger the action of the resource, if applicable
     * @param isRow     true if a row is selected, false if a column is selected
     * @param index     index of the selected row or column
     * @param zeros     a map of the chosen resources to take, if choices are applicable
     * @param shelves   a map of the shelves where to add the taken resources, if possible
     * @return          true if possible, false otherwise
     */
    private boolean canTakeResources(Player player, boolean isRow, int index, Map<ResourceType, Integer> zeros,
                                     Map<ResourceType, Map<Shelf, Integer>> shelves) {
        return false;
    }
}
