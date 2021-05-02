package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

import java.util.List;

public class ReqSwapShelves implements Message {
    private List<Integer> shelves;

    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
