package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.view.View;

public class ReqQuit implements VCEvent {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
