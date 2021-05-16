package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventEmitter {
    private final transient Map<Class<? extends Event>, Set<EventListener<? extends Event>>> listeners;

    public EventEmitter(Set<Class<? extends Event>> eventTypes) {
        listeners = eventTypes.stream().collect(Collectors.toMap(Function.identity(), eventType -> new HashSet<>()));
    }

    public <T extends Event> void register(Class<T> eventType, EventListener<T> listener) {
        if (!listeners.containsKey(eventType))
            throw new IllegalArgumentException();
        listeners.get(eventType).add(listener);
    }

    public <T extends Event> void unregister(Class<T> eventType, EventListener<T> listener) {
        if (!listeners.containsKey(eventType))
            throw new IllegalArgumentException();
        listeners.get(eventType).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void emit(T event) {
        Class<? extends Event> eventType = event.getClass();
        if (!listeners.containsKey(eventType))
            throw new IllegalArgumentException();
        listeners.get(eventType).forEach(listener -> ((EventListener<T>) listener).on(event));
    }
}
