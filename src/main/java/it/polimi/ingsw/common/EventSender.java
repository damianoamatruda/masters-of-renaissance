package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

public interface EventSender {
    void send(Event event);

    void stop();
}
