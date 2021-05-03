package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.view.View;

public class ReqPlayersCount implements VCEvent {
    private int count;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public int getCount() {
        return count;
    }
}
