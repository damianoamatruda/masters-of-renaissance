package it.polimi.ingsw.server;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.View;

public class VirtualView extends View {
    public VirtualView(NetworkHandler networkHandler) {
        setListeners(networkHandler);
    }

    private void setListeners(NetworkHandler networkHandler) {
        setErrNewGameEventListener(networkHandler::send);
        setErrNicknameEventListener(networkHandler::send);
        setResQuitEventListener(networkHandler::send);
        setUpdateBookedSeatsEventListener(networkHandler::send);
        setUpdateJoinGameEventListener(networkHandler::send);
        setUpdateServerUnavailableEventListener(networkHandler::send);
        setErrActionEventListener(networkHandler::send);
        setErrActiveLeaderDiscardedEventListener(networkHandler::send);
        setErrBuyDevCardEventListener(networkHandler::send);
        setErrCardRequirementsEventListener(networkHandler::send);
        setErrInitialChoiceEventListener(networkHandler::send);
        setErrNoSuchEntityEventListener(networkHandler::send);
        setErrObjectNotOwnedEventListener(networkHandler::send);
        setErrReplacedTransRecipeEventListener(networkHandler::send);
        setErrResourceReplacementEventListener(networkHandler::send);
        setErrResourceTransferEventListener(networkHandler::send);
        setUpdateActionEventListener(networkHandler::send);
        setUpdateActionTokenEventListener(networkHandler::send);
        setUpdateCurrentPlayerEventListener(networkHandler::send);
        setUpdateDevCardGridEventListener(networkHandler::send);
        setUpdateDevSlotEventListener(networkHandler::send);
        setUpdateFaithPointsEventListener(networkHandler::send);
        setUpdateGameEventListener(networkHandler::send);
        setUpdateGameEndEventListener(networkHandler::send);
        setUpdateLastRoundEventListener(networkHandler::send);
        setUpdateActivateLeaderEventListener(networkHandler::send);
        setUpdateLeadersHandCountEventListener(networkHandler::send);
        setUpdateMarketEventListener(networkHandler::send);
        setUpdatePlayerStatusEventListener(networkHandler::send);
        setUpdateResourceContainerEventListener(networkHandler::send);
        setUpdateSetupDoneEventListener(networkHandler::send);
        setUpdateVaticanSectionEventListener(networkHandler::send);
        setUpdateVictoryPointsEventListener(networkHandler::send);
        setUpdateLeadersHandEventListener(networkHandler::send);
    }
}
