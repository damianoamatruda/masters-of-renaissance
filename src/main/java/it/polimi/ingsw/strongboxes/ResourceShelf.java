package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.ResourceType;

/**
 * This class represents a container of resources of a specific type in limited quantity.
 */
public class ResourceShelf extends Shelf {
    /** The specific type of resources the shelf can contain. */
    private ResourceType resType;

    /**
     * Initializes the shelf specifying the type of resources it can contain.
     *
     * @param size      the maximum quantity of resources in the the shelf
     * @param resType   the specific type of resources in the shelf
     */
    public ResourceShelf(int size, ResourceType resType) {
        super(size);
        this.resType = resType;
    }

    /**
     * Adds a resource of the given type to the shelf.
     *
     * @param resType   the type of the resource to add
     */
    public void addResource(ResourceType resType) {

    }
}
