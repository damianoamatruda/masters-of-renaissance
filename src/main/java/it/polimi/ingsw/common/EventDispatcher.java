package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventDispatcher {
    private final transient Map<Class<? extends Event>, Set<EventListener<? extends Event>>> listeners;

    public EventDispatcher() {
        listeners = new HashMap<>();
    }

    public <T extends Event> void addEventListener(Class<T> eventType, EventListener<T> listener) {
        if (!listeners.containsKey(eventType))
            listeners.put(eventType, new HashSet<>());
        listeners.get(eventType).add(listener);
    }

    public <T extends Event> void removeEventListener(Class<T> eventType, EventListener<T> listener) {
        if (!listeners.containsKey(eventType))
            return;
        listeners.get(eventType).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatch(T event) {
        if (!listeners.containsKey(event.getClass()))
            return;
        listeners.get(event.getClass()).forEach(listener -> ((EventListener<T>) listener).on(event));
    }
}
