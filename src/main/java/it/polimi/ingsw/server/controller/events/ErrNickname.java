package it.polimi.ingsw.server.controller.events;

public class ErrNickname implements MVEvent {
    private final String message;

    public ErrNickname(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
