package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public class ErrCommunication implements MVEvent {
    private final String message;

    public ErrCommunication(String message) {
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
