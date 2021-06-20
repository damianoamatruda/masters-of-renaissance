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

    /**
     * Returns the specific type of resources the shelf can contain.
     *
     * @return the bounded type of resources
     */
    public ResourceType getBoundedResType() {
        return boundedResType;
    }

    @Override
    public void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        resMap = sanitizeResourceMap(resMap);

        if (resMap.size() == 0)
            return;

        ResourceType resType = getShelfResourceType(resMap);

        if (!resType.equals(this.boundedResType))
            throw new IllegalResourceTransferException(resType, true, Kind.BOUNDED_RESTYPE_DIFFER);

        super.addResources(resMap);
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), getSize(), getResourceType().isPresent() ? Map.of(getResourceType().get().getName(), getQuantity()) : Map.of(), boundedResType.getName());
    }
}
