package it.polimi.ingsw;

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

    /**
     * Checks if it is possible to swap the content of two shelves.
     *
     * @param s1    the first shelf
     * @param s2    the second shelf
     * @return      true if possible, false otherwise
     */
    private static boolean canSwap(Shelf s1, Shelf s2) {
        return false;
    }

    /**
     * Checks if it is possible to add a resource of the given type in the shelf.
     *
     * @param resType   the type of the resource to add
     * @return          true if possible, false otherwise
     */
    private boolean canAddResource(ResourceType resType) {
        return false;
    }

    /**
     * Checks if it is possible to remove a resource of the given type from the shelf.
     *
     * @param resType   the type of the resource to remove
     * @return          true if possible, false otherwise
     */
    private boolean canRemoveResource(ResourceType resType) {
        return false;
    }
}
