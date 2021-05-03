package it.polimi.ingsw.server.controller.events;

public class ErrCommunication implements MVEvent {
    private final String message;

    public ErrCommunication(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
