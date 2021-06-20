package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException.Kind;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a group of warehouse shelves of growing size.
 *
 * @see ResourceContainerGroup
 * @see WarehouseShelf
 */
public class Warehouse implements ResourceContainerGroup {
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

    @Override
    public List<ResourceContainer> getResourceContainers() {
        return List.copyOf(shelves);
    }

    /**
     * Returns the shelves inside the warehouse
     *
     * @return the list of the shelves
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
        /**
         * Initializes the shelf by specifying the warehouse containing it.
         *
         * @param warehouse the warehouse containing the shelf
         * @param size      the maximum number of resources in the shelf
         */
        public WarehouseShelf(Warehouse warehouse, int size) {
            super(size);
            this.group = warehouse;
        }

        /**
         * Copy constructor. Makes a deep copy of a warehouse shelf.
         *
         * @param warehouseShelf the warehouse shelf to copy
         */
        public WarehouseShelf(WarehouseShelf warehouseShelf) {
            super(warehouseShelf);
            group = warehouseShelf.group;
        }

        @Override
        public ResourceContainer copy() {
            return new WarehouseShelf(this);
        }

        @Override
        public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
            ResourceType resType = getShelfResourceType(resMap);
            resMap = sanitizeResourceMap(resMap);

            if (group.getResourceContainers().stream()
                    .filter(c -> !c.equals(this))
                    .map(ResourceContainer::getResourceTypes)
                    .anyMatch(resTypes -> resTypes.contains(resType)))
                throw new IllegalResourceTransferException(resType, true, Kind.DUPLICATE_BOUNDED_RESOURCE);

            super.addResources(resMap);
        }
    }
}
