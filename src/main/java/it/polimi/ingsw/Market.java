package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Shelf;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a special container of resources that can be taken from the player.
 * It consists of a grid of resources together with a slide containing a single resource.
 */
public class Market {
    /** The resources in the grid. */
    private final List<List<ResourceType>> grid;

    /** The resource in the slide. */
    private ResourceType slide;

    /**
     * Initializes the market by placing the resources randomly in the grid and in the slide.
     *
     * @param resources map of the resources to put inside the market
     * @param colsCount number of columns in the grid
     */
    public Market(Map<ResourceType, Integer> resources, int colsCount) {
        List<ResourceType> resourcesList = new ArrayList<>();
        resources.forEach((r, q) -> IntStream.range(0, q).forEach(i -> resourcesList.add(r)));

        /* Checks that colsCount is not zero. */
        if (colsCount == 0)
            throw new RuntimeException();

        /* Checks that the resulting grid, therefore excluding the slide, is not empty. */
        if (resourcesList.size() <= 1)
            throw new RuntimeException();

        /* Checks that the resulting grid, therefore excluding the slide, is full. */
        if (resourcesList.size() % colsCount != 1)
            throw new RuntimeException();

        Collections.shuffle(resourcesList);

        grid = new ArrayList<>();
        slide = resourcesList.remove(0);

        for (ResourceType r : resourcesList) {
            if (grid.isEmpty() || grid.get(grid.size() - 1).size() == colsCount) {
                List<ResourceType> row = new ArrayList<>();
                grid.add(row);
            }
            grid.get(grid.size() - 1).add(r);
        }
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return  the number of rows
     */
    public int getRowsCount() {
        return grid.size();
    }

    /**
     * Returns the number of columns in the grid.
     *
     * @return  the number of columns
     */
    public int getColsCount() {
        return grid.get(0).size();
    }

    /**
     * Returns the types of resources in the grid.
     *
     * @return  the types of resources
     */
    public List<List<ResourceType>> getGrid() {
        return grid;
    }

    /**
     * Returns the type of the resource in the slide.
     *
     * @return  the type of the resource
     */
    public ResourceType getSlide() {
        return slide;
    }

    /**
     * Takes a chosen row or column of resources from the grid. Each resource is added in a given shelf, if possible,
     * and triggers an action on the player, if applicable.
     *
     * After taking the resources, the chosen row or column is shifted one place from respectively the right or bottom,
     * the resource in the slide takes the uncovered place in the grid and the leftover resource goes into the slide.
     *
     * @param player        the player on which to trigger the action of the resource, if applicable
     * @param isRow         true if a row is selected, false if a column is selected
     * @param index         index of the selected row or column
     * @param zerosRep      a map of the chosen resources to take, if choices are applicable
     * @param shelves       a map of the shelves where to add the taken resources, if possible
     * @throws Exception    if it is not possible
     */
    public void takeResources(Player player, boolean isRow, int index, Map<ResourceType, Integer> zerosRep,
                              Map<Shelf, Map<ResourceType, Integer>> shelves) throws Exception {
        if (isRow && index >= getRowsCount() || !isRow && index >= getColsCount())
            throw new RuntimeException();

        Map<ResourceType, Integer> output = IntStream
                .range(0, isRow ? getColsCount() : getRowsCount())
                .mapToObj(i -> isRow ? grid.get(index).get(i) : grid.get(i).get(index))
                .collect(Collectors.toMap(resType -> resType, resType -> 1, Integer::sum));

        (new Production(new HashMap<>(), output, true))
                .activate(player, new HashMap<>(), zerosRep, new HashMap<>(), new HashMap<>(shelves));

        shift(isRow, index);
    }

    /**
     * Shifts a row or column one place from respectively the right or bottom.
     *
     * The resource in the slide takes the uncovered place in the grid and the leftover resource goes into the slide.
     *
     * @param isRow true if a row is to shift, false if a column is to shift
     * @param index index of the row or column to shift
     */
    private void shift(boolean isRow, int index) {
        ResourceType oldSlide = slide;
        if (isRow) {
            slide = grid.get(index).get(0);
            for (int i = 1; i < getColsCount(); i++)
                grid.get(index).set(i-1, grid.get(index).get(i));
            grid.get(index).set(getColsCount() - 1, oldSlide);
        } else {
            slide = grid.get(0).get(index);
            for (int i = 1; i < getRowsCount(); i++)
                grid.get(i-1).set(index, grid.get(i).get(index));
            grid.get(getRowsCount() - 1).set(index, oldSlide);
        }
    }
}
