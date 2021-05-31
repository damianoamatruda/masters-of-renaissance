package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This interface represents a container of storable resources.
 */
public abstract class ResourceContainer extends EventDispatcher {
    private static final AtomicInteger idCounter = new AtomicInteger();

    private final int id;

    /** The group containing the resource container. */
    protected transient ResourceContainerGroup group;

    public ResourceContainer() {
        this.id = idCounter.getAndIncrement();
        this.group = null;
    }

    public ResourceContainer(int id, ResourceContainerGroup group) {
        this.id = id;
        this.group = group;
    }

    /**
     * Swaps the content of two resource containers.
     *
     * @param container1 the first resource container
     * @param container2 the second resource container
     * @throws IllegalResourceTransferException if the resource containers cannot be swapped
     */
    public static void swap(ResourceContainer container1, ResourceContainer container2) throws IllegalResourceTransferException {
        ResourceContainerGroup group = null;
        ResourceContainerGeneralGroup groupClone = null;

        ResourceContainer clone1 = container1.copy();
        ResourceContainer clone2 = container2.copy();

        if (container1.getGroup().isPresent()) {
            group = container1.getGroup().get();
            groupClone = new ResourceContainerGeneralGroup(group);
            groupClone.replaceResourceContainer(container1, clone1);
            clone1.setGroup(groupClone);
        }

        if (container2.getGroup().isPresent()) {
            if (!container2.getGroup().get().equals(group)) {
                group = container2.getGroup().get();
                groupClone = new ResourceContainerGeneralGroup(group);
            }
            groupClone.replaceResourceContainer(container2, clone2);
            clone2.setGroup(groupClone);
        }

        clone1.removeResources(container1.getResourceMap());
        clone2.removeResources(container2.getResourceMap());

        clone1.addResources(container2.getResourceMap());
        clone2.addResources(container1.getResourceMap());

        container1.removeResources(container1.getResourceMap());
        container2.removeResources(container2.getResourceMap());

        container1.addResources(clone1.getResourceMap());
        container2.addResources(clone2.getResourceMap());
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
     * Returns the map of the resources contained.
     *
     * @return the resource map
     */
    public abstract Map<ResourceType, Integer> getResourceMap();

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

    public Optional<ResourceContainerGroup> getGroup() {
        return Optional.ofNullable(group);
    }

    public void setGroup(ResourceContainerGroup group) {
        this.group = group;
    }

    /**
     * Converts the container to a map.
     *
     * @return a map representing the container
     */
    public abstract ReducedResourceContainer reduce();
}
