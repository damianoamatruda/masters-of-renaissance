package it.polimi.ingsw.common.events.vcevents;

/** Client request to swap two shelves. */
public class ReqSwapShelves implements VCEvent {
    /** The first shelf. */
    private final int shelf1;

    /** The second shelf. */
    private final int shelf2;

    /**
     * Class constructor.
     * 
     * @param shelf1 the first shelf
     * @param shelf2 the second shelf
     */
    public ReqSwapShelves(int shelf1, int shelf2) {
        this.shelf1 = shelf1;
        this.shelf2 = shelf2;
    }

    /**
     * @return the first shelf
     */
    public int getShelf1() {
        return shelf1;
    }

    /**
     * @return the second shelf
     */
    public int getShelf2() {
        return shelf2;
    }
}
