package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public class ReqPlayersCount implements VCEvent {
    private final int count;

    public ReqPlayersCount(int count) {
        this.count = count;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public int getCount() {
        return count;
    }
}
