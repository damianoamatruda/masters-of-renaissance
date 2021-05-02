package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

@FunctionalInterface
public interface Message {
    void handle(View view);
}
