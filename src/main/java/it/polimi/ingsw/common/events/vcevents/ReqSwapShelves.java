package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

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

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the first shelf
     */
    public int getS1() {
        return shelf1;
    }

    /**
     * @return the second shelf
     */
    public int getS2() {
        return shelf2;
    }
}
