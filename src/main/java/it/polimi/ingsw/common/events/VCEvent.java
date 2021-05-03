package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

@FunctionalInterface
public interface VCEvent {
    void handle(View view);
}
