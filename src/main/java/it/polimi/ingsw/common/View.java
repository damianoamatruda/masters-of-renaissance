package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.*;

/** Interface defining the 'View' role in the MVC architecture. */
public interface View /*extends ModelObserver, ControllerObservable, CommunicationObserver*/ {
    // ModelObserver section
    /**
     * @param event the response to a disconnection request.
     *              Handled specifically due to special routines being needed.
     */
    void update(ResGoodbye event);

    /**
     * @param event the event cointaining the new state
     */
    void update(MVEvent event);

    
    
    // ControllerObservable section
    
    void notify(GoodBye event);

    void notify(ReqJoin event);

    void notify(ReqActivateLeader event);

    void notify(ReqActivateProduction event);

    void notify(ReqBuyDevCard event);

    void notify(ReqDiscardLeader event);

    void notify(ReqChooseLeaders event);

    void notify(ReqNewGame event);

    void notify(ReqChooseResources event);

    void notify(ReqSwapShelves event);

    void notify(ReqTakeFromMarket event);

    void notify(ReqTurnEnd event);
}
