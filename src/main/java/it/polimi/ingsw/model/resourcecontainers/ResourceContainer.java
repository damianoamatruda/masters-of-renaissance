package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.Set;

/**
 * This interface represents a container of storable resources.
 */
public interface ResourceContainer {
    /**
     * Copy method.
     *
     * @return a deep copy of the resource container
     */
    ResourceContainer copy();

    /**
     * Returns the types of the resources contained.
     *
     * @return the resource types
     */
    Set<ResourceType> getResourceTypes();

    /**
     * Returns the quantity of the resources contained.
     *
     * @return the total quantity
     */
    int getQuantity();

    /**
     * Returns the quantity of resources of the given type.
     *
     * @param resType the type of the resources
     * @return the quantity
     */
    int getResourceQuantity(ResourceType resType);

    /**
     * Adds a resource of the given type.
     *
     * @param resType the resource to add
     * @throws IllegalResourceTransferException if the container is full
     */
    void addResource(ResourceType resType) throws IllegalResourceTransferException;

    /**
     * Removes a resource of the given type.
     *
     * @param resType the resource to remove
     * @throws IllegalResourceTransferException if the container is empty
     */
    void removeResource(ResourceType resType) throws IllegalResourceTransferException;

    /**
     * Returns whether the resource container is empty.
     *
     * @return <code>true</code> if the resource container contains no resources; <code>false</code> otherwise.
     */
    boolean isEmpty();
}
