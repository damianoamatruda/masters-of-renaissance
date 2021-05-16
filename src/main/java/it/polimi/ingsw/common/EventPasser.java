package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

public interface EventPasser {
    void on(Event event);

    void stop();
}
