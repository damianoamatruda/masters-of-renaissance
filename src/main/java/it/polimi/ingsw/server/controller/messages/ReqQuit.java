package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ReqQuit implements Message {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
