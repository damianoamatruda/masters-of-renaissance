package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ErrNickname implements MVEvent {
    private final String message;

    public ErrNickname(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    public String getMessage() {
        return message;
    }
}
