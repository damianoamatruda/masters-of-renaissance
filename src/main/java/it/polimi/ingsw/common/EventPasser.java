package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

public interface EventPasser extends EventListener<Event> {
    void stop();
}
