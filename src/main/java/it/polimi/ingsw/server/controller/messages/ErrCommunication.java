package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ErrCommunication implements Message {
    private final String message;

    public ErrCommunication(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
    }

    public String getMessage() {
        return message;
    }
}
