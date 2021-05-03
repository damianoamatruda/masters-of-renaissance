package it.polimi.ingsw.server.controller.events;

public class ErrAction implements MVEvent {
    private final String message;

    public ErrAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
