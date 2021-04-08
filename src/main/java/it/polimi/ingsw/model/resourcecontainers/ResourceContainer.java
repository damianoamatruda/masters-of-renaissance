package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.Set;

/**
 * This interface represents a container of resources.
 */
public interface ResourceContainer {
    /**
     * Overridable copy method.
     *
     * @return  a resource container that is a deep copy of itself
     */
    ResourceContainer copy();

    /**
     * Returns the types of the resources contained.
     *
     * @return  the types of the resources
     */
    Set<ResourceType> getResourceTypes();

    /**
     * Returns the quantity of the resources contained.
     *
     * @return  the quantity of the resources
     */
    int getQuantity();

    /**
     * Returns the quantity of resources of the given type.
     *
     * @param resType   the type of the resources
     * @return          the quantity of the resources
     */
    int getResourceQuantity(ResourceType resType);

    /**
     * Adds a storable resource of the given type
     *
     * @param resType       the resource to add
     * @throws Exception    if it is not possible
     */
    void addResource(ResourceType resType) throws Exception;

    /**
     * Removes a storable resource of the given type
     *
     * @param resType       the resource to remove
     * @throws Exception    if it is not possible
     */
    void removeResource(ResourceType resType) throws Exception;

    /**
     * Adds the content of a resource container.
     *
     * @param resourceContainer the resource container from which to get the resources to add
     * @throws Exception        if it is not possible
     */
    void addAll(ResourceContainer resourceContainer) throws Exception;

    /**
     * Returns whether the resource container is empty.
     *
     * @return  true if the resource container contains no resources, false otherwise
     */
    boolean isEmpty();
}
