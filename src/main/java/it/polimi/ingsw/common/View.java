package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.vcevents.*;

import java.util.Set;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventEmitter {
    public View() {
        super(Set.of(ReqGoodbye.class, ReqJoin.class, ReqNewGame.class, ReqChooseLeaders.class, ReqChooseResources.class, ReqSwapShelves.class, ReqActivateLeader.class, ReqDiscardLeader.class, ReqTakeFromMarket.class, ReqBuyDevCard.class, ReqActivateProduction.class, ReqEndTurn.class));
    }

    public abstract void registerToProtocol(EventEmitter protocol);

    public abstract void unregisterToProtocol(EventEmitter protocol);

    public abstract void registerToModel(EventEmitter model);

    public abstract void unregisterToModel(EventEmitter model);
}
