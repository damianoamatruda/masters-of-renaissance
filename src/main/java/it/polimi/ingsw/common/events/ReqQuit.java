package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public class ReqQuit implements VCEvent {
    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }
}
