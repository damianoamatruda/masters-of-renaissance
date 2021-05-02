package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ErrAction implements Message {
    private final String message;

    public ErrAction(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
    }

    public String getMessage() {
        return message;
    }
}
