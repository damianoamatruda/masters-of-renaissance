package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;

public interface MVEvent extends Event {
    void handle(View view);
}
