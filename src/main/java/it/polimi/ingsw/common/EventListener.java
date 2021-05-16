package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

@FunctionalInterface
public interface EventListener<T extends Event> {
    void on(T event);
}
