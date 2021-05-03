package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.view.View;

@FunctionalInterface
public interface VCEvent {
    void handle(View view);
}
