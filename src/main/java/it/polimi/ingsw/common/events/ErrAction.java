package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ErrAction implements MVEvent {
    private final String message;

    public ErrAction(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    public String getMessage() {
        return message;
    }
}
