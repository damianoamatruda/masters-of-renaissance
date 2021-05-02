package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.view.View;

public class ReqSwapShelves implements Message {
    private Shelf s1;
    private Shelf s2;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public Shelf getS1() {
        return s1;
    }

    public Shelf getS2() {
        return s2;
    }
}
