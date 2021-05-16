package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.vcevents.*;

import java.util.Set;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventEmitter implements EventListener<MVEvent> {
    public View() {
        super(Set.of(ReqGoodbye.class, ReqJoin.class, ReqNewGame.class, ReqChooseLeaders.class, ReqChooseResources.class, ReqSwapShelves.class, ReqActivateLeader.class, ReqDiscardLeader.class, ReqTakeFromMarket.class, ReqBuyDevCard.class, ReqActivateProduction.class, ReqEndTurn.class));
    }

    public abstract void registerToModelLobby(EventEmitter model);

    public abstract void unregisterToModelLobby(EventEmitter model);

    public abstract void registerToModelGameContext(EventEmitter model);

    public abstract void unregisterToModelGameContext(EventEmitter model);

    public abstract void registerToPrivateModelPlayer(EventEmitter model);

    public abstract void unregisterToPrivateModelPlayer(EventEmitter model);
}
