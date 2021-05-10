package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Client request to swap two shelves. */
public class ReqSwapShelves implements VCEvent {
    /** The first shelf. */
    private final int s1;
    /** The second shelf. */
    private final int s2;

    /**
     * Class constructor.
     * 
     * @param s1 the first shelf
     * @param s2 the second shelf
     */
    public ReqSwapShelves(int s1, int s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the first shelf
     */
    public int getS1() {
        return s1;
    }

    /**
     * @return the second shelf
     */
    public int getS2() {
        return s2;
    }
}
