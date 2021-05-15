package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.*;

/** Interface defining the 'View' role in the MVC architecture. */
public interface View /*extends ModelObserver, ControllerObservable, CommunicationObserver*/ {
    // ModelObserver section

    /**
     * @param event the response to a disconnection request. Handled specifically due to special routines being needed.
     */
    void update(ResGoodbye event);

    void update(ResWelcome event);

    void update(ErrAction event);

    void update(ErrProtocol event);

    void update(ErrRuntime event);

    void update(UpdateActionToken event);

    void update(UpdateBookedSeats event);

    void update(UpdateCurrentPlayer event);

    void update(UpdateDevCardGrid event);

    void update(UpdateDevCardSlot event);

    void update(UpdateFaithPoints event);

    void update(UpdateGameEnd event);

    void update(UpdateGameResume event);

    void update(UpdateGameStart event);

    void update(UpdateJoinGame event);

    void update(UpdateLastRound event);

    void update(UpdateLeader event);

    void update(UpdateLeadersHand event);

    void update(UpdateMarket event);

    void update(UpdatePlayerStatus event);

    void update(UpdateResourceContainer event);

    void update(UpdateSetupDone event);

    void update(UpdateVaticanSection event);

    void update(UpdateVictoryPoints event);

    // ControllerObservable section

    void notify(ReqGoodbye event);

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

    void notify(ReqEndTurn event);
}
