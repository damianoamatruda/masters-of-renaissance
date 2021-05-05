package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public class ReqSwapShelves implements VCEvent {
    private final int s1;
    private final int s2;

    public ReqSwapShelves(int s1, int s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public int getS1() {
        return s1;
    }

    public int getS2() {
        return s2;
    }
}
