package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventDispatcher implements EventListener<MVEvent> {
    public abstract void registerToModelLobby(EventDispatcher model);

    public abstract void unregisterToModelLobby(EventDispatcher model);

    public abstract void registerToModelGameContext(EventDispatcher model);

    public abstract void unregisterToModelGameContext(EventDispatcher model);

    public abstract void registerToPrivateModelPlayer(EventDispatcher model);

    public abstract void unregisterToPrivateModelPlayer(EventDispatcher model);
}
