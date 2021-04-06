package it.polimi.ingsw.resourcecontainers;

import it.polimi.ingsw.resourcetypes.ResourceType;

/**
 * This class represents a shelf in a warehouse, that is a shelf that cannot contain a resource that is contained also
 * in another shelf of the same warehouse.
 *
 * @see Warehouse
 */
public class WarehouseShelf extends Shelf {
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
     * Copy constructor. Makes a deep copy of a WarehouseShelf.
     *
     * @param warehouseShelf    the WarehouseShelf to copy
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
    public void addResource(ResourceType resType) throws Exception {
        if (warehouse.getShelves().stream()
                .filter(s -> !s.equals(this) && !s.isEmpty())
                .anyMatch(s -> s.getResType().equals(resType)))
            throw new Exception();
        super.addResource(resType);
    }
}
