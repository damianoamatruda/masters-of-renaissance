package it.polimi.ingsw;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a container of resources.
 */
public class Strongbox {
    /** The map of the contained resources. */
    private final Map<ResourceType, Integer> resources;

    /**
     * Initializes the strongbox.
     */
    public Strongbox() {
        resources = new HashMap<>();
    }

    /**
     * Returns the quantity of resources of the given type.
     *
     * @param resType   the type of the resources
     * @return          the quantity of the resources
     */
    public int getResourceQuantity(ResourceType resType) {
        return 0;
    }

    /**
     * Adds a resource of the given type.
     *
     * @param resType   the resource to add
     */
    public void addResource(ResourceType resType) {

    }

    /**
     * Removes a resource of the given type.
     *
     * @param resType   the resource to remove
     */
    public void removeResource(ResourceType resType) {

    }

    /**
     * Adds the content of a strongbox.
     *
     * @param strongbox the strongbox from which to get the resources to add
     */
    public void addStrongbox(Strongbox strongbox) {

    }

    /**
     * Returns if the strongbox is empty.
     *
     * @return true if the strongbox contains no resources, false otherwise
     */
    public boolean isEmpty() {
        return false;
    }
}
