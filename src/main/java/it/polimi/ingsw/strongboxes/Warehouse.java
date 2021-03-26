package it.polimi.ingsw.strongboxes;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a container of shelves of growing size.
 *
 * @see WarehouseShelf
 */
public class Warehouse {
    /** The shelves inside the warehouse. */
    private final List<WarehouseShelf> shelves;

    /**
     * Initializes the warehouse by constructing the shelves of growing size.
     */
    public Warehouse(int shelvesCount) {
        shelves = new ArrayList<>();
        for (int i = 1; i <= shelvesCount; i++)
            shelves.add(new WarehouseShelf(this, i));
    }

    /**
     * Returns the number of shelves inside the warehouse
     *
     * @return  the number of shelves
     */
    public int getShelvesCount() {
        return shelves.size();
    }

    /**
     * Returns the shelves inside the warehouse
     *
     * @return  a list of the shelves
     */
    public List<WarehouseShelf> getShelves() {
        return shelves;
    }
}
