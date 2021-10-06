package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents objects capable of dispatching Events.
 */
public class EventDispatcher {
    /**
     * List of listeners holding the event handlers that will be called when notified of a dispatched event.
     */
    private final transient Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners;

    /** Class constructor. */
    public EventDispatcher() {
        listeners = new HashMap<>();
    }

    /**
     * Hooks the listener to this dispatcher so it can receive events.
     *
     * @param <T>
     * @param eventType the class of the events listened for.
     * @param listener  the listener to register.
     */
    public <T extends Event> void addEventListener(Class<T> eventType, EventListener<? super T> listener) {
        if (!listeners.containsKey(eventType))
            listeners.put(eventType, new ArrayList<>());
        listeners.get(eventType).add(listener);
    }

    /**
     * Unregisters a listener, preventing it from receiving Events.
     *
     * @param <T>
     * @param eventType the class of Event the listener was listening to.
     * @param listener  the listener to deregister.
     */
    public <T extends Event> void removeEventListener(Class<T> eventType, EventListener<? super T> listener) {
        if (!listeners.containsKey(eventType))
            return;
        listeners.get(eventType).remove(listener);
    }

    /**
     * Dispatches an event to all listeners listening for Events of that class.
     *
     * @param <T>
     * @param event the event to dispatch.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatch(T event) {
        for (Class<? extends Event> eventType : listeners.keySet())
            if (eventType.isAssignableFrom(event.getClass()))
                listeners.get(eventType).forEach(listener -> ((EventListener<? super T>) listener).on(event));
    }
}
