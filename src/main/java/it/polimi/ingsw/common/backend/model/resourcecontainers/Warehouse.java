package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException.Kind;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Initializes the warehouse based on the given warehouse shelves.
     *
     * @param shelves the warehouse shelves
     */
    public Warehouse(List<WarehouseShelf> shelves) {
        this.shelves = new ArrayList<>(shelves);
    }

    /**
     * Returns the shelves inside the warehouse
     *
     * @return a list of the shelves
     */
    public List<WarehouseShelf> getShelves() {
        return List.copyOf(shelves);
    }

    /**
     * This class represents a shelf in a warehouse, that is a shelf that cannot contain a resource that is contained
     * also in another shelf of the same warehouse.
     *
     * @see Warehouse
     */
    public static class WarehouseShelf extends Shelf {
        /** The warehouse containing the shelf. */
        transient private final Warehouse warehouse;

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
            /* Create new Warehouse by replacing original shelf with its copy. This allows addResources to continue
             * working using .equals() */
            List<WarehouseShelf> shelves = new ArrayList<>(warehouseShelf.warehouse.shelves);
            shelves.replaceAll(s -> s.equals(warehouseShelf) ? this : s);
            warehouse = new Warehouse(shelves);
        }

        @Override
        public ResourceContainer copy() {
            return new WarehouseShelf(this);
        }

        @Override
        public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
            if (resMap.values().stream().noneMatch(v -> v > 0))
                return;
            if (resMap.values().stream().filter(v -> v > 0).count() != 1)
                throw new RuntimeException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)
            ResourceType resType = resMap.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).findAny().orElseThrow();

            if (warehouse.shelves.stream()
                    .filter(s -> !s.equals(this))
                    .map(Shelf::getResourceType)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(r -> r.equals(resType)))
                throw new IllegalResourceTransferException(resType, true, Kind.DUPLICATEBOUNDEDRESOURCE);
            super.addResources(resMap);
        }
    }
}
