package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException.Kind;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.Map;

/**
 * This class represents a limited container of resources of a specific type.
 */
public class ResourceShelf extends Shelf {
    /** The specific type of resources the shelf can contain. */
    private final ResourceType boundedResType;

    /**
     * Initializes the shelf specifying the type of resources it can contain.
     *
     * @param boundedResType the specific type of resources in the shelf
     * @param size           the maximum quantity of resources in the shelf
     */
    public ResourceShelf(ResourceType boundedResType, int size) {
        super(size);
        this.boundedResType = boundedResType;
    }

    /**
     * Copy constructor. Makes a deep copy of a resource shelf.
     *
     * @param resourceShelf the resource shelf to copy
     */
    public ResourceShelf(ResourceShelf resourceShelf) {
        super(resourceShelf);
        boundedResType = resourceShelf.boundedResType;
    }

    @Override
    public ResourceContainer copy() {
        return new ResourceShelf(this);
    }

    @Override
    public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        if (resMap.values().stream().noneMatch(v -> v > 0))
            return;
        if (resMap.values().stream().filter(v -> v > 0).count() != 1)
            throw new RuntimeException(); // TODO: Add more specific exception (this is the case of resMap with more than one resType)
        ResourceType resType = resMap.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).findAny().orElseThrow();

        if (!resType.equals(this.boundedResType))
            throw new IllegalResourceTransferException(resType, true, Kind.BOUNDEDRESTYPEDIFFER);
        super.addResources(resMap);
    }

    /**
     * Returns the specific type of resources the shelf can contain.
     *
     * @return the bounded type of resources
     */
    public ResourceType getBoundedResType() {
        return boundedResType;
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), -1, getResourceType().isPresent() ? Map.of(getResourceType().get().getName(), getQuantity()) : Map.of(), boundedResType.getName());
    }
}
