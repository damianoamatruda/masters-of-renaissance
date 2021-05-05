package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public class ErrAction implements MVEvent {
    private final String message;

    public ErrAction(String message) {
        this.message = message;
    }

    @Override
    public void handle(ModelObserver view) {
        view.update(this);
    }

    public String getMessage() {
        return message;
    }
}
