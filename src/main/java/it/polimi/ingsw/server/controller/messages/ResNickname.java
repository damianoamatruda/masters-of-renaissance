package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ResNickname implements Message {
    private final boolean isFirst;

    public ResNickname(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void handle(View view) {
    }

    public boolean isFirst() {
        return isFirst;
    }
}
