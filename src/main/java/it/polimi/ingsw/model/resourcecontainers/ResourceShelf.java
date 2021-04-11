package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.Set;

/**
 * This class represents a limited container of resources of a specific type.
 */
public class ResourceShelf extends Shelf {
    /** The specific type of resources the shelf can contain. */
    private final ResourceType boundedResType;

    /**
     * Initializes the shelf specifying the type of resources it can contain.
     *
     * @param boundedResType    the specific type of resources in the shelf
     * @param size              the maximum quantity of resources in the shelf
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
    public void addResource(ResourceType resType) throws IllegalResourceTransferException {
        if (!resType.equals(this.boundedResType))
            throw new IllegalResourceTransferException();
        super.addResource(resType);
    }

    @Override
    public void addAll(ResourceContainer resourceContainer) throws IllegalResourceTransferException {
        if (!resourceContainer.isEmpty() && !resourceContainer.getResourceTypes().equals(Set.of(boundedResType)))
            throw new IllegalResourceTransferException();
        super.addAll(resourceContainer);
    }

    /**
     * Returns the specific type of resources the shelf can contain.
     *
     * @return  the bounded type of resources
     */
    public ResourceType getBoundedResType() {
        return boundedResType;
    }
}
