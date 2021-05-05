package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ErrController implements MVEvent {
    private final String message;

    public ErrController(Exception e) {
        this.message = e.getMessage();
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    public String getMessage() {
        return message;
    }
}
