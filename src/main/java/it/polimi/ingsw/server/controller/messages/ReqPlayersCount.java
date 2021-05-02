package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ReqPlayersCount implements Message {
    private int count;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public int getCount() {
        return count;
    }
}
