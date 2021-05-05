package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public class ResGoodbye implements MVEvent {
    @Override
    public void handle(ModelObserver view) {
        view.update(this);
    }
}
