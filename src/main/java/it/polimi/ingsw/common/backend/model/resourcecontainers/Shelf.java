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
        super(shelf.getId(), shelf.group);
        size = shelf.size;
        resType = shelf.resType;
        quantity = shelf.quantity;
    }

    public static ResourceType getShelfResourceType(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        validateStorableResourceMap(resMap);
        resMap = sanitizeResourceMap(resMap);

        if (resMap.size() != 1)
            throw new IllegalArgumentException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)

        return resMap.keySet().stream().findAny().orElseThrow();
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
        return resType != null ? Map.of(resType, quantity) : Map.of();
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resType.equals(this.resType) ? quantity : 0;
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
    public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        ResourceType resType = getShelfResourceType(resMap);
        resMap = sanitizeResourceMap(resMap);

        if (this.resType != null && !resType.equals(this.resType))
            throw new IllegalResourceTransferException(resType, true, Kind.BOUNDED_RESTYPE_DIFFER);

        if (this.quantity + resMap.get(resType) > size)
            throw new IllegalResourceTransferException(resType, true, Kind.CAPACITY_REACHED);

        this.resType = resType;
        this.quantity += resMap.get(resType);

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        ResourceType resType = getShelfResourceType(resMap);
        resMap = sanitizeResourceMap(resMap);

        if (this.resType != null && !resType.equals(this.resType))
            throw new IllegalResourceTransferException(resType, false, Kind.BOUNDED_RESTYPE_DIFFER);

        if (this.quantity < resMap.get(resType))
            throw new IllegalResourceTransferException(resType, false, Kind.CAPACITY_REACHED);

        if (this.quantity == resMap.get(resType))
            this.resType = null;

        this.quantity -= resMap.get(resType);

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), getSize(), resType != null ? Map.of(resType.getName(), quantity) : Map.of(), resType != null ? resType.getName() : null);
    }
}
