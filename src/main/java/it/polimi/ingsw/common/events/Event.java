package it.polimi.ingsw.common.events;

public interface Event {
    public default boolean isPrioritized() {
        return false;
    }
}
