package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventDispatcher implements EventListener<MVEvent> {
    /** The event passer of the view. */
    protected EventPasser eventPasser;

    /**
     * Sets the event passer.
     *
     * @param eventPasser the event passer
     */
    public void setEventPasser(EventPasser eventPasser) {
        this.eventPasser = eventPasser;
    }

    public abstract void registerToModelGame(EventDispatcher game);

    public abstract void unregisterToModelGame(EventDispatcher game);

    public abstract void registerToModelPlayer(EventDispatcher player);

    public abstract void unregisterToModelPlayer(EventDispatcher player);
}
