package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;

@FunctionalInterface
public interface VCEvent extends Event {
    void handle(View view);
}
