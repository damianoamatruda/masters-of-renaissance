package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ReqBuyDevCard implements Message {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
