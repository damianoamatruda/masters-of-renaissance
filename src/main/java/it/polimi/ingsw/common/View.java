package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventDispatcher implements EventListener<MVEvent> {
    public abstract void registerToModelGame(EventDispatcher game);

    public abstract void unregisterToModelGame(EventDispatcher game);

    public abstract void registerToModelPlayer(EventDispatcher player);

    public abstract void unregisterToModelPlayer(EventDispatcher player);
}
