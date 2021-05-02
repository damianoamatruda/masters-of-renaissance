package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ErrController implements Message {
    private final String message;

    public ErrController(Exception e) {
        this.message = e.getMessage();
    }

    @Override
    public void handle(View view) {
    }

    public String getMessage() {
        return message;
    }
}
