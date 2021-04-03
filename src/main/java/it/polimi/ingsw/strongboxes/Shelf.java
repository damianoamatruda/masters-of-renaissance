package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.resourcetypes.ResourceType;

/**
 * This class represents a container of resources of the same type in limited quantity.
 */
public class Shelf extends Strongbox {
    /** The maximum quantity of resources in the shelf. */
    private final int size;

    /** The type of the resources in the shelf. */
    private ResourceType resType;

    /**
     * Initializes the shelf specifying the size.
     *
     * @param size  the maximum quantity of resources in the shelf
     */
    public Shelf(int size) {
        super();
        this.size = size;
        this.resType = null;
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
    public void addResource(ResourceType resType) throws Exception {
        if (this.resType != null && !resType.equals(this.resType))
            throw new Exception();
        if (isFull())
            throw new Exception();
        this.resType = resType;
        super.addResource(resType);
    }

    @Override
    public void removeResource(ResourceType resType) throws Exception {
        super.removeResource(resType);
        if (isEmpty())
            this.resType = null;
    }

    @Override
    public void addStrongbox(Strongbox strongbox) throws Exception {
        if (strongbox.resources.keySet().stream().anyMatch(r -> !r.equals(resType)))
            throw new Exception();
        if (size - resources.get(resType) < strongbox.getResourceQuantity(resType))
            throw new Exception();
        super.addStrongbox(strongbox);
    }

    /**
     * Returns whether the shelf is full.
     *
     * @return  true if full, otherwise false
     */
    public boolean isFull() {
        return getQuantity() == size;
    }
}
