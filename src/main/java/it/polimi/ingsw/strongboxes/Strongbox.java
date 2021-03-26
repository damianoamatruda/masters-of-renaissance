package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.resourcetypes.ResourceType;

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
     * @param resType       the resource to add
     * @throws Exception    if it is not possible
     */
    public void addResource(ResourceType resType) throws Exception {

    }

    /**
     * Removes a resource of the given type.
     *
     * @param resType       the resource to remove
     * @throws Exception    if it is not possible
     */
    public void removeResource(ResourceType resType) throws Exception {

    }

    /**
     * Adds the content of a strongbox.
     *
     * @param strongbox     the strongbox from which to get the resources to add
     * @throws Exception    if it is not possible
     */
    public void addStrongbox(Strongbox strongbox) throws Exception {

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
