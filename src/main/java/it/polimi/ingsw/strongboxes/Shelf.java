package it.polimi.ingsw.strongboxes;

import it.polimi.ingsw.ResourceType;

/**
 * This class represents a container of resources of the same type in limited quantity.
 */
public class Shelf extends Strongbox {
    /** The maximum quantity of resources in the shelf. */
    private final int size;

    /**
     * Initializes the shelf specifying the size.
     *
     * @param size  the maximum quantity of resources in the shelf
     */
    public Shelf(int size) {
        super();
        this.size = size;
    }

    /**
     * Swaps the content of two shelves, if possible.
     *
     * @param s1    the first shelf
     * @param s2    the second shelf
     */
    public static void swap(Shelf s1, Shelf s2) {

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
    public ResourceType getResource() {
        return null;
    }

    /**
     * Returns the quantity of the resources in the shelf.
     *
     * @return  the quantity of the resources
     */
    public int getQuantity() {
        return super.getResourceQuantity(this.getResource());
    }

    @Override
    public void addResource(ResourceType resType) {
        super.addResource(resType);
    }

    @Override
    public void removeResource(ResourceType resType) {
        super.removeResource(resType);
    }
}
