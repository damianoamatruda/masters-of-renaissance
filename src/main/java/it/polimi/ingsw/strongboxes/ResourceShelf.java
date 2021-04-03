package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.resourcetypes.ResourceType;

/**
 * This class represents a container of resources of a specific type in limited quantity.
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
     * Copy constructor. Makes a deep copy of a ResourceShelf.
     *
     * @param resourceShelf the ResourceShelf to copy
     */
    public ResourceShelf(ResourceShelf resourceShelf) {
        super(resourceShelf);
        boundedResType = resourceShelf.boundedResType;
    }

    @Override
    public Strongbox copy() {
        return new ResourceShelf(this);
    }

    @Override
    public void addResource(ResourceType resType) throws Exception {
        if (!resType.equals(this.boundedResType))
            throw new Exception();
        super.addResource(resType);
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
