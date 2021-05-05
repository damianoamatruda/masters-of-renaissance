package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public interface VCEvent {
    void handle(ControllerObservable view);
}
