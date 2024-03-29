package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

/**
 * This class represents objects that listen to Events.
 */
@FunctionalInterface
public interface EventListener<T extends Event> {
    /**
     * Event handler.
     *
     * @param event the event to be handled.
     */
    void on(T event);
}
