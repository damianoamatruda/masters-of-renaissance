package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.*;

/**
 * This class represents a container of resources of the same type in limited quantity.
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
     * Copy constructor. Makes a deep copy of a Shelf.
     *
     * @param shelf the Shelf to copy
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
     * @throws Exception    if it is not possible
     */
    public static void swap(Shelf s1, Shelf s2) throws Exception {
        if (s1.getQuantity() > s2.getSize() || s1.getSize() < s2.getQuantity())
            throw new Exception();
        ResourceType r1 = s1.resType;
        ResourceType r2 = s2.resType;
        int q1 = s1.getQuantity();
        int q2 = s2.getQuantity();
        for (int i = 0; i < q1; i++)
            s1.removeResource(r1);
        for (int i = 0; i < q2; i++)
            s2.removeResource(r2);
        for (int i = 0; i < q2; i++)
            s1.addResource(r2);
        for (int i = 0; i < q1; i++)
            s2.addResource(r1);
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
    public ResourceType getResType() {
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
        if (isFull())
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
        this.quantity--;
        if (isEmpty())
            this.resType = null;
    }

    @Override
    public void addAll(ResourceContainer resourceContainer) throws Exception {
        if (resourceContainer.getResourceTypes().size() > 1)
            throw new Exception();
        if (resType != null && resourceContainer.getResourceTypes().stream().anyMatch(r -> !r.equals(resType)))
            throw new Exception();
        if (resourceContainer.getResourceQuantity(resType) > size - quantity)
            throw new Exception();
        quantity += resourceContainer.getResourceQuantity(resType);
    }

    @Override
    public boolean isEmpty() {
        return quantity == 0;
    }

    /**
     * Returns whether the shelf is full.
     *
     * @return  <code>true</code> if full; <code>false</code> otherwise.
     */
    public boolean isFull() {
        return quantity == size;
    }
}
