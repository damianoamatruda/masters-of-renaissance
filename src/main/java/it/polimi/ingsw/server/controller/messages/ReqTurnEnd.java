package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ReqTurnEnd implements Message {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
