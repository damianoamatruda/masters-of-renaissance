package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ReqQuit implements VCEvent {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
