package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a special container of resources that can be taken from the player.
 * It consists of a grid of resources together with a slide containing a single resource.
 */
public class Market {
    /** The resources in the grid. */
    private final List<List<ResourceType>> gridResources;

    /** The resource in the slide. */
    private ResourceType slideResource;

    /**
     * Initializes the market by placing the resources randomly in the grid and in the slide.
     *
     * @param resources map of the resources to put inside the market
     * @param colsCount number of columns in the grid
     */
    public Market(Map<ResourceType, Integer> resources, int colsCount) {
        // TODO: Implement
        gridResources = new ArrayList<>();
        slideResource = null;
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return  the number of rows
     */
    public static int getRowsCount() {
        return 0;
    }

    /**
     * Returns the number of columns in the grid.
     *
     * @return  the number of columns
     */
    public static int getColsCount() {
        return 0;
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
        // TODO: Implement
    }
}
