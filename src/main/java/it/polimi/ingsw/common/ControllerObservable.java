package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.*;

public interface ControllerObservable {
    void notify(ReqQuit event);

    void notify(ReqNickname event);

    void notify(ReqActivateLeader event);

    void notify(ReqActivateProduction event);

    void notify(ReqBuyDevCard event);

    void notify(ReqDiscardLeader event);

    void notify(ReqChooseLeaders event);

    void notify(ReqPlayersCount event);

    void notify(ReqChooseResources event);

    void notify(ReqSwapShelves event);

    void notify(ReqTakeFromMarket event);

    void notify(ReqTurnEnd event);
}
