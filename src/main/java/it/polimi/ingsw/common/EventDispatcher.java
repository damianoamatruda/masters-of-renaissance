package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final transient Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners;

    public EventDispatcher() {
        listeners = new HashMap<>();
    }

    public <T extends Event> void addEventListener(Class<T> eventType, EventListener<? super T> listener) {
        if (!listeners.containsKey(eventType))
            listeners.put(eventType, new ArrayList<>());
        listeners.get(eventType).add(listener);
    }

    public <T extends Event> void removeEventListener(Class<T> eventType, EventListener<? super T> listener) {
        if (!listeners.containsKey(eventType))
            return;
        listeners.get(eventType).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatch(T event) {
        for (Class<? extends Event> eventType : listeners.keySet())
            if (eventType.isAssignableFrom(event.getClass()))
                listeners.get(eventType).forEach(listener -> ((EventListener<? super T>) listener).on(event));
    }
}
