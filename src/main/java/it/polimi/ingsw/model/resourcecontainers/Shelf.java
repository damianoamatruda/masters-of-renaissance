package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.*;

/**
 * This class represents a finite container of resources of the same type.
 */
public class Shelf implements ResourceContainer {
    /** The maximum quantity of resources in the shelf. */
    private final int size;

    /** The type of the resources in the shelf. */
    private ResourceType resType;

    /** The quantity of the resources in the shelf. */
    private int quantity;

    /**
     * Initializes the shelf specifying the size.
     *
     * @param size  the maximum quantity of resources in the shelf
     */
    public Shelf(int size) {
        this.size = size;
        this.resType = null;
        this.quantity = 0;
    }

    /**
     * Copy constructor. Makes a deep copy of a shelf.
     *
     * @param shelf the shelf to copy
     */
    public Shelf(Shelf shelf) {
        size = shelf.size;
        resType = shelf.resType;
        quantity = shelf.quantity;
    }

    @Override
    public ResourceContainer copy() {
        return new Shelf(this);
    }

    /**
     * Swaps the content of two shelves if possible.
     *
     * @param s1            the first shelf
     * @param s2            the second shelf
     * @throws Exception    if the shelves cannot be swapped
     */
    public static void swap(Shelf s1, Shelf s2) throws Exception {
        ResourceContainer clone1 = s1.copy();
        ResourceContainer clone2 = s2.copy();

        for (int i = 0; i < s1.quantity; i++)
            clone1.removeResource(s1.resType);
        for (int i = 0; i < s2.quantity; i++)
            clone2.removeResource(s2.resType);

        for (int i = 0; i < s2.quantity; i++)
            clone1.addResource(s2.resType);
        for (int i = 0; i < s1.quantity; i++)
            clone2.addResource(s1.resType);

        s1.resType = clone1.getResourceTypes().stream().findFirst().orElse(null);
        s1.quantity = clone1.getQuantity();

        s2.resType = clone2.getResourceTypes().stream().findFirst().orElse(null);
        s2.quantity = clone2.getQuantity();
    }

    /**
     * Returns the size of the shelf.
     *
     * @return  the maximum quantity of resources in the shelf
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the type of the resources in the shelf.
     *
     * @return  the type of the resources
     */
    public ResourceType getResourceType() {
        return resType;
    }

    @Override
    public Set<ResourceType> getResourceTypes() {
        return resType != null ? Set.of(resType) : Set.of();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resType.equals(this.resType) ? quantity : 0;
    }

    @Override
    public void addResource(ResourceType resType) throws Exception {
        if (this.resType != null && !resType.equals(this.resType))
            throw new Exception();
        if (!resType.isStorable())
            throw new RuntimeException();
        if (quantity == size)
            throw new Exception();
        this.resType = resType;
        this.quantity++;
    }

    @Override
    public void removeResource(ResourceType resType) throws Exception {
        if (this.resType != null && !resType.equals(this.resType))
            throw new Exception();
        if (quantity == 0)
            throw new Exception();
        if (!resType.isStorable())
            throw new RuntimeException();
        if (quantity == 1)
            this.resType = null;
        this.quantity--;
    }

    @Override
    public void addAll(ResourceContainer resourceContainer) throws Exception {
        if (resourceContainer.isEmpty())
            return;
        if (resourceContainer.getResourceTypes().size() > 1)
            throw new Exception();
        if (resourceContainer.getQuantity() > size - quantity)
            throw new Exception();
        if (resType != null && !resourceContainer.getResourceTypes().equals(Set.of(resType)))
            throw new Exception();
        resType = resourceContainer.getResourceTypes().stream().findFirst().orElse(null);
        quantity += resourceContainer.getQuantity();
    }

    @Override
    public boolean isEmpty() {
        return quantity == 0;
    }

    /**
     * Returns whether the shelf is full.
     *
     * @return  <code>true</code> if the shelf contains all possible resources, <code>false</code> otherwise.
     */
    public boolean isFull() {
        return quantity == size;
    }
}
