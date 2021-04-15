package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.ArrayList;
import java.util.Collections;
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
     *
     * @param shelvesCount the number of shelves in the warehouse
     */
    public Warehouse(int shelvesCount) {
        shelves = new ArrayList<>();
        for (int i = 1; i <= shelvesCount; i++)
            shelves.add(new WarehouseShelf(this, i));
    }

    /**
     * Copy constructor. Makes a deep copy of a warehouse.
     *
     * @param warehouse the warehouse to copy
     */
    public Warehouse(Warehouse warehouse) {
        shelves = new ArrayList<>();
        for (WarehouseShelf warehouseShelf : warehouse.shelves)
            shelves.add(new WarehouseShelf(warehouseShelf));
    }

    /**
     * Returns the shelves inside the warehouse
     *
     * @return a list of the shelves
     */
    public List<WarehouseShelf> getShelves() {
        return Collections.unmodifiableList(shelves);
    }

    /**
     * This class represents a shelf in a warehouse, that is a shelf that cannot contain a resource that is contained
     * also in another shelf of the same warehouse.
     *
     * @see Warehouse
     */
    public static class WarehouseShelf extends Shelf {
        /** The warehouse containing the shelf. */
        private final Warehouse warehouse;

        /**
         * Initializes the shelf by specifying the warehouse containing it.
         *
         * @param warehouse the warehouse containing the shelf
         * @param size      the maximum number of resources in the shelf
         */
        public WarehouseShelf(Warehouse warehouse, int size) {
            super(size);
            this.warehouse = warehouse;
        }

        /**
         * Copy constructor. Makes a deep copy of a warehouse shelf.
         *
         * @param warehouseShelf the warehouse shelf to copy
         */
        public WarehouseShelf(WarehouseShelf warehouseShelf) {
            super(warehouseShelf);
            warehouse = warehouseShelf.warehouse;
        }

        @Override
        public ResourceContainer copy() {
            return new WarehouseShelf(this);
        }

        @Override
        public void addResource(ResourceType resType) throws IllegalResourceTransferException {
            if (warehouse.shelves.stream()
                    .filter(s -> !s.equals(this))
                    .anyMatch(s -> s.getResourceType() != null && s.getResourceType().equals(resType)))
                throw new IllegalResourceTransferException();
            super.addResource(resType);
        }
    }
}
