package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public interface MVEvent {
    void handle(ModelObserver view);
}
