package it.polimi.ingsw.server.controller.events;

public class ErrController implements MVEvent {
    private final String message;

    public ErrController(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
