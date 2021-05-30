package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException.Kind;
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
        super();
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
    public Map<ResourceType, Integer> getResourceMap() {
        return Map.of(resType, quantity);
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resType.equals(this.resType) ? quantity : 0;
    }

    @Override
    public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        // TODO: Share this portion with other methods
        // TODO: Make checks of null resource types in resource maps
        if (resMap.values().stream().noneMatch(v -> v > 0))
            return;
        if (resMap.values().stream().filter(v -> v > 0).count() != 1)
            throw new RuntimeException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)
        ResourceType resType = resMap.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).findAny().orElseThrow();

        if (this.resType != null && !resType.equals(this.resType))
            throw new IllegalResourceTransferException(resType, true, Kind.BOUNDED_RESTYPE_DIFFER);
        if (!resType.isStorable())
            throw new IllegalResourceTransferException(resType, true, Kind.NON_STORABLE);
        if (this.quantity + resMap.get(resType) > size)
            throw new IllegalResourceTransferException(resType, true, Kind.CAPACITY_REACHED);

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
            throw new IllegalResourceTransferException(resType, false, Kind.BOUNDED_RESTYPE_DIFFER);
        if (!resType.isStorable())
            throw new IllegalResourceTransferException(resType, false, Kind.NON_STORABLE);
        if (this.quantity < resMap.get(resType))
            throw new IllegalResourceTransferException(resType, false, Kind.CAPACITY_REACHED);
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
        return new ReducedResourceContainer(getId(), getSize(), resType != null ? Map.of(resType.getName(), quantity) : Map.of(), resType != null ? resType.getName() : null);
    }
}
