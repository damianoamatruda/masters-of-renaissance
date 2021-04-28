package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a special container of resources that can be taken from the player. It consists of a grid of
 * resources together with a slide containing a single resource.
 */
public class Market {
    /** The resources in the grid. */
    private final List<List<ResourceType>> grid;

    /** The type of the resources that can be replaced. */
    private final ResourceType replaceableResType;

    /** The resource in the slide. */
    private ResourceType slide;

    /**
     * Initializes the market by placing the resources randomly in the grid and in the slide.
     *
     * @param resources          the map of the resources to put inside the market
     * @param colsCount          the number of columns in the grid
     * @param replaceableResType the type of the resources that can be replaced
     */
    public Market(Map<ResourceType, Integer> resources, int colsCount, ResourceType replaceableResType) {
        List<ResourceType> resourcesList = new ArrayList<>();
        resources.forEach((r, q) -> IntStream.range(0, q).forEach(i -> resourcesList.add(r)));

        if (colsCount <= 0)
            throw new IllegalArgumentException("Cannot create market: illegal number of columns: 0");

        if (resourcesList.size() == 0)
            throw new IllegalArgumentException("Cannot create market: illegal number of resources: 0");

        /* Check that the resulting grid is full. */
        if (colsCount > 1 && resourcesList.size() % colsCount != 1)
            throw new IllegalArgumentException(
                String.format("Cannot create market: %d resources cannot be cleanly divided into %d columns + slide",
                    resourcesList.size(), colsCount));

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

        this.replaceableResType = replaceableResType;
    }

    /**
     * Takes a chosen row or column of resources from the grid. Each resource is added in a given shelf, if possible,
     * and triggers an action on the player, if applicable.
     * <p>
     * After taking the resources, the chosen row or column is shifted one place from respectively the right or bottom,
     * the resource in the slide takes the uncovered place in the grid and the leftover resource goes into the slide.
     *
     * @param game         the game the player is playing in
     * @param player       the player on which to trigger the action of the resource, if applicable
     * @param isRow        <code>true</code> if a row is selected; <code>false</code> if a column is selected
     * @param index        index of the selected row or column
     * @param replacements a map of the chosen resources to take, if choices are applicable
     * @param shelves      a map of the shelves where to add the taken resources, if possible
     * @throws IllegalMarketTransferException if it is not possible
     */
    public void takeResources(Game game, Player player, boolean isRow, int index, Map<ResourceType, Integer> replacements,
                              Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalMarketTransferException {
        // TODO: Make sure that shelves are of type Shelf

        if (isRow && index >= getRowsCount() || !isRow && index >= getColsCount())
            throw new IllegalArgumentException(
                String.format("Cannot take resources: %s %d does not exist, limit is %d",
                    isRow ? "row" : "column", index, isRow ? grid.get(0).size() : grid.size()));

        Map<ResourceType, Integer> output = IntStream
                .range(0, isRow ? getColsCount() : getRowsCount())
                .mapToObj(i -> isRow ? grid.get(index).get(i) : grid.get(i).get(index))
                .collect(Collectors.toMap(resType -> resType, resType -> 1, Integer::sum));

        for (LeaderCard leader : player.getLeaders())
            output = leader.replaceMarketResources(replaceableResType, output, replacements);
        output.remove(replaceableResType);

        try {
            new ProductionGroup(List.of(
                    new ProductionGroup.ProductionRequest(
                            new Production(Map.of(), 0, Set.of(), output, 0, Set.of(), true),
                            Map.of(), Map.of(), Map.of(), shelves)
            )).activate(game, player);
        } catch (IllegalProductionActivationException e) {
            throw new IllegalMarketTransferException(e);
        }

        shift(isRow, index);
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return the number of rows
     */
    public int getRowsCount() {
        return grid.size();
    }

    /**
     * Returns the number of columns in the grid.
     *
     * @return the number of columns
     */
    public int getColsCount() {
        return grid.get(0).size();
    }

    /**
     * Returns the types of resources in the grid.
     *
     * @return the types of resources
     */
    public List<List<ResourceType>> getGrid() {
        List<List<ResourceType>> grid = new ArrayList<>();
        for (List<ResourceType> row : this.grid)
            grid.add(List.copyOf(row));
        return List.copyOf(grid);
    }

    /**
     * Returns the type of the resource in the slide.
     *
     * @return the type of the resource
     */
    public ResourceType getSlide() {
        return slide;
    }

    /**
     * Returns the type of the resources that can be replaced.
     *
     * @return the type of the replaceable resources
     */
    public ResourceType getReplaceableResType() {
        return replaceableResType;
    }

    /**
     * Shifts a row or column one place from respectively the right or bottom. The resource in the slide takes the
     * uncovered place in the grid and the leftover resource goes into the slide.
     *
     * @param isRow <code>true</code> if a row is to shift; <code>false</code> if a column is to shift.
     * @param index index of the row or column to shift
     */
    private void shift(boolean isRow, int index) {
        ResourceType oldSlide = slide;
        if (isRow) {
            slide = grid.get(index).get(0);
            for (int i = 1; i < getColsCount(); i++)
                grid.get(index).set(i - 1, grid.get(index).get(i));
            grid.get(index).set(getColsCount() - 1, oldSlide);
        } else {
            slide = grid.get(0).get(index);
            for (int i = 1; i < getRowsCount(); i++)
                grid.get(i - 1).set(index, grid.get(i).get(index));
            grid.get(getRowsCount() - 1).set(index, oldSlide);
        }
    }
}
