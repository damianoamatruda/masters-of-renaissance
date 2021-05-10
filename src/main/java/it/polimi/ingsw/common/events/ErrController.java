package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ErrController implements MVEvent {
    private final String message;

    public ErrController(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getClass().getName());
        if (e.getMessage() != null)
            stringBuilder.append(": ").append(e.getMessage());
        this.message = stringBuilder.toString();
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    public String getMessage() {
        return message;
    }
}
