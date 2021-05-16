package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents a finite container of resources of the same type.
 */
public class Shelf extends ResourceContainer {
    /** The maximum quantity of resources in the shelf. */
    private final int size;

    /** The type of the resources in the shelf. */
    private ResourceType resType;

    /** The quantity of the resources in the shelf. */
    private int quantity;

    /**
     * Initializes the shelf specifying the size.
     *
     * @param size the maximum quantity of resources in the shelf
     */
    public Shelf(int size) {
        this.size = size;
        this.resType = null;
        this.quantity = 0;
    }

    /**
     * Copy constructor. Makes a deep copy of a shelf.
     *
     * @param shelf the shelf to copy
     */
    public Shelf(Shelf shelf) {
        size = shelf.size;
        resType = shelf.resType;
        quantity = shelf.quantity;
    }

    /**
     * Swaps the content of two shelves.
     *
     * @param shelf1 the first shelf
     * @param shelf2 the second shelf
     * @throws IllegalResourceTransferException if the shelves cannot be swapped
     */
    public static void swap(Shelf shelf1, Shelf shelf2) throws IllegalResourceTransferException {
        ResourceContainer clone1 = shelf1.copy();
        ResourceContainer clone2 = shelf2.copy();

        // TODO: Make checks of null resource types in resource maps also in other methods using resource maps
        if (shelf1.resType != null)
            clone1.removeResources(Map.of(shelf1.resType, shelf1.quantity));
        if (shelf2.resType != null)
            clone2.removeResources(Map.of(shelf2.resType, shelf2.quantity));

        if (shelf2.resType != null)
            clone1.addResources(Map.of(shelf2.resType, shelf2.quantity));
        if (shelf1.resType != null)
            clone2.addResources(Map.of(shelf1.resType, shelf1.quantity));

        shelf1.resType = clone1.getResourceTypes().stream().findAny().orElse(null);
        shelf1.quantity = clone1.getQuantity();

        shelf2.resType = clone2.getResourceTypes().stream().findAny().orElse(null);
        shelf2.quantity = clone2.getQuantity();
    }

    @Override
    public ResourceContainer copy() {
        return new Shelf(this);
    }

    /**
     * Returns the size of the shelf.
     *
     * @return the maximum quantity of resources in the shelf
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the type of the resources in the shelf.
     *
     * @return the optional type of the resources
     */
    public Optional<ResourceType> getResourceType() {
        return Optional.ofNullable(resType);
    }

    @Override
    public Set<ResourceType> getResourceTypes() {
        return resType != null ? Set.of(resType) : Set.of();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resType.equals(this.resType) ? quantity : 0;
    }

    @Override
    public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        // TODO: Share this portion with other methods
        if (resMap.values().stream().noneMatch(v -> v > 0))
            return;
        if (resMap.values().stream().filter(v -> v > 0).count() != 1)
            throw new RuntimeException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)
        ResourceType resType = resMap.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).findAny().orElseThrow();

        if (this.resType != null && !resType.equals(this.resType))
            throw new IllegalResourceTransferException(resType, true, this.resType);
        if (!resType.isStorable())
            throw new IllegalResourceTransferException(resType, true);
        if (this.quantity + resMap.get(resType) > size)
            throw new IllegalResourceTransferException(resType, true, this);

        this.resType = resType;
        this.quantity += resMap.get(resType);

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void addResource(ResourceType resType) throws IllegalResourceTransferException {
        addResources(Map.of(resType, 1));
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        if (resMap.values().stream().noneMatch(v -> v > 0))
            return;
        if (resMap.values().stream().filter(v -> v > 0).count() != 1)
            throw new RuntimeException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)
        ResourceType resType = resMap.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).findAny().orElseThrow();

        if (this.resType != null && !resType.equals(this.resType))
            throw new IllegalResourceTransferException(resType, false, this.resType);
        if (!resType.isStorable())
            throw new IllegalResourceTransferException(resType, false);

        if (this.quantity < resMap.get(resType))
            throw new IllegalResourceTransferException(resType, false, this);
        if (this.quantity == resMap.get(resType))
            this.resType = null;
        this.quantity -= resMap.get(resType);

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void removeResource(ResourceType resType) throws IllegalResourceTransferException {
        removeResources(Map.of(resType, 1));
    }

    @Override
    public boolean isEmpty() {
        return quantity == 0;
    }

    /**
     * Returns whether the shelf is full.
     *
     * @return <code>true</code> if the shelf contains all possible resources; <code>false</code> otherwise.
     */
    public boolean isFull() {
        return quantity == size;
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), resType != null ? Map.of(resType.getName(), quantity) : Map.of(), resType != null ? resType.getName() : null);
    }
}
