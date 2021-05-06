package it.polimi.ingsw.server.model.resourcecontainers;

import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This interface represents a container of storable resources.
 */
public abstract class ResourceContainer {
    private static final AtomicInteger idCounter = new AtomicInteger();

    private final int id;

    public ResourceContainer() {
        id = idCounter.getAndIncrement();
    }

    public int getId() {
        return id;
    }

    /**
     * Copy method.
     *
     * @return a deep copy of the resource container
     */
    public abstract ResourceContainer copy();

    /**
     * Returns the types of the resources contained.
     *
     * @return the resource types
     */
    public abstract Set<ResourceType> getResourceTypes();

    /**
     * Returns the quantity of the resources contained.
     *
     * @return the total quantity
     */
    public abstract int getQuantity();

    /**
     * Returns the quantity of resources of the given type.
     *
     * @param resType the type of the resources
     * @return the quantity
     */
    public abstract int getResourceQuantity(ResourceType resType);

    /**
     * Adds resources.
     *
     * @param resMap the resources to add
     * @throws IllegalResourceTransferException if the container is full
     */
    public abstract void addResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException;

    /**
     * Adds a resource of the given type.
     *
     * @param resType the resource to add
     * @throws IllegalResourceTransferException if the container is full
     */
    public abstract void addResource(ResourceType resType) throws IllegalResourceTransferException;

    /**
     * Removes resources.
     *
     * @param resMap the resources to remove
     * @throws IllegalResourceTransferException if the container is full
     */
    public abstract void removeResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException;

    /**
     * Removes a resource of the given type.
     *
     * @param resType the resource to remove
     * @throws IllegalResourceTransferException if the container is empty
     */
    public abstract void removeResource(ResourceType resType) throws IllegalResourceTransferException;

    /**
     * Returns whether the resource container is empty.
     *
     * @return <code>true</code> if the resource container contains no resources; <code>false</code> otherwise.
     */
    public abstract boolean isEmpty();
}
