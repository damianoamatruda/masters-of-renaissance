package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ErrNickname implements Message {
    private final String message;

    public ErrNickname(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
    }

    public String getMessage() {
        return message;
    }
}
