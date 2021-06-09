package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.netevents.ReqGoodbye;
import it.polimi.ingsw.common.events.vcevents.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class NetworkHandler extends EventDispatcher implements Runnable {
    protected final Socket socket;
    protected final NetworkProtocol protocol;
    protected PrintWriter out;
    protected BufferedReader in;
    protected volatile boolean listening;

    private final EventListener<ResQuit> resQuitEventListener = this::send;
    private final EventListener<UpdateBookedSeats> updateBookedSeatsEventListener = this::send;
    private final EventListener<UpdateJoinGame> updateJoinGameEventListener = this::send;
    private final EventListener<ErrNewGame> errNewGameEventListener = this::send;
    private final EventListener<ErrNickname> errNicknameEventListener = this::send;
    private final EventListener<ErrAction> errActionEventListener = this::send;
    private final EventListener<ErrActiveLeaderDiscarded> errActiveLeaderDiscardedEventListener = this::send;
    private final EventListener<ErrBuyDevCard> errBuyDevCardEventListener = this::send;
    private final EventListener<ErrCardRequirements> errCardRequirementsEventListener = this::send;
    private final EventListener<ErrInitialChoice> errInitialChoiceEventListener = this::send;
    private final EventListener<ErrNoSuchEntity> errNoSuchEntityEventListener = this::send;
    private final EventListener<ErrObjectNotOwned> errObjectNotOwnedEventListener = this::send;
    private final EventListener<ErrReplacedTransRecipe> errReplacedTransRecipeEventListener = this::send;
    private final EventListener<ErrResourceReplacement> errResourceReplacementEventListener = this::send;
    private final EventListener<ErrResourceTransfer> errResourceTransferEventListener = this::send;
    private final EventListener<UpdateAction> updateActionEventListener = this::send;
    private final EventListener<UpdateActionToken> updateActionTokenEventListener = this::send;
    private final EventListener<UpdateCurrentPlayer> updateCurrentPlayerEventListener = this::send;
    private final EventListener<UpdateDevCardGrid> updateDevCardGridEventListener = this::send;
    private final EventListener<UpdateDevCardSlot> updateDevCardSlotEventListener = this::send;
    private final EventListener<UpdateFaithPoints> updateFaithPointsEventListener = this::send;
    private final EventListener<UpdateGame> updateGameEventListener = this::send;
    private final EventListener<UpdateGameEnd> updateGameEndEventListener = this::send;
    private final EventListener<UpdateLastRound> updateLastRoundEventListener = this::send;
    private final EventListener<UpdateActivateLeader> updateActivateLeaderEventListener = this::send;
    private final EventListener<UpdateLeadersHandCount> updateLeadersHandCountEventListener = this::send;
    private final EventListener<UpdateMarket> updateMarketEventListener = this::send;
    private final EventListener<UpdatePlayer> updatePlayerEventListener = this::send;
    private final EventListener<UpdatePlayerStatus> updatePlayerStatusEventListener = this::send;
    private final EventListener<UpdateResourceContainer> updateResourceContainerEventListener = this::send;
    private final EventListener<UpdateSetupDone> updateSetupDoneEventListener = this::send;
    private final EventListener<UpdateVaticanSection> updateVaticanSectionEventListener = this::send;
    private final EventListener<UpdateVictoryPoints> updateVictoryPointsEventListener = this::send;
    private final EventListener<UpdateLeadersHand> updateLeadersHandEventListener = this::send;
    private final EventListener<ReqActivateProduction> reqActivateProductionEventListener = this::send;
    private final EventListener<ReqBuyDevCard> reqBuyDevCardEventListener = this::send;
    private final EventListener<ReqChooseLeaders> reqChooseLeadersEventListener = this::send;
    private final EventListener<ReqChooseResources> reqChooseResourcesEventListener = this::send;
    private final EventListener<ReqEndTurn> reqEndTurnEventListener = this::send;
    private final EventListener<ReqJoin> reqJoinEventListener = this::send;
    private final EventListener<ReqLeaderAction> reqLeaderActionEventListener = this::send;
    private final EventListener<ReqNewGame> reqNewGameEventListener = this::send;
    private final EventListener<ReqQuit> reqQuitEventListener = this::send;
    private final EventListener<ReqSwapShelves> reqSwapShelvesEventListener = this::send;
    private final EventListener<ReqTakeFromMarket> reqTakeFromMarketEventListener = this::send;

    public NetworkHandler(Socket socket, NetworkProtocol protocol) {
        this.socket = socket;
        this.protocol = protocol;
        this.out = null;
        this.in = null;
        this.listening = false;
    }

    public void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, resQuitEventListener);
        view.addEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        view.addEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        view.addEventListener(ErrNewGame.class, errNewGameEventListener);
        view.addEventListener(ErrNickname.class, errNicknameEventListener);
        view.addEventListener(ErrAction.class, errActionEventListener);
        view.addEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        view.addEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        view.addEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        view.addEventListener(ErrInitialChoice.class, errInitialChoiceEventListener);
        view.addEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        view.addEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        view.addEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        view.addEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        view.addEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        view.addEventListener(UpdateAction.class, updateActionEventListener);
        view.addEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        view.addEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        view.addEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        view.addEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        view.addEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        view.addEventListener(UpdateGame.class, updateGameEventListener);
        view.addEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        view.addEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        view.addEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        view.addEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        view.addEventListener(UpdateMarket.class, updateMarketEventListener);
        view.addEventListener(UpdatePlayer.class, updatePlayerEventListener);
        view.addEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        view.addEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        view.addEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        view.addEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        view.addEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
        view.addEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
    }

    public void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, resQuitEventListener);
        view.removeEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        view.removeEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        view.removeEventListener(ErrNewGame.class, errNewGameEventListener);
        view.removeEventListener(ErrNickname.class, errNicknameEventListener);
        view.removeEventListener(ErrAction.class, errActionEventListener);
        view.removeEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        view.removeEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        view.removeEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        view.removeEventListener(ErrInitialChoice.class, errInitialChoiceEventListener);
        view.removeEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        view.removeEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        view.removeEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        view.removeEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        view.removeEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        view.removeEventListener(UpdateAction.class, updateActionEventListener);
        view.removeEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        view.removeEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        view.removeEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        view.removeEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        view.removeEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        view.removeEventListener(UpdateGame.class, updateGameEventListener);
        view.removeEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        view.removeEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        view.removeEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        view.removeEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        view.removeEventListener(UpdateMarket.class, updateMarketEventListener);
        view.removeEventListener(UpdatePlayer.class, updatePlayerEventListener);
        view.removeEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        view.removeEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        view.removeEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        view.removeEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        view.removeEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
        view.removeEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
    }

    public void registerOnVC(EventDispatcher view) {
        view.addEventListener(ReqActivateProduction.class, reqActivateProductionEventListener);
        view.addEventListener(ReqBuyDevCard.class, reqBuyDevCardEventListener);
        view.addEventListener(ReqChooseLeaders.class, reqChooseLeadersEventListener);
        view.addEventListener(ReqChooseResources.class, reqChooseResourcesEventListener);
        view.addEventListener(ReqEndTurn.class, reqEndTurnEventListener);
        view.addEventListener(ReqJoin.class, reqJoinEventListener);
        view.addEventListener(ReqLeaderAction.class, reqLeaderActionEventListener);
        view.addEventListener(ReqNewGame.class, reqNewGameEventListener);
        view.addEventListener(ReqQuit.class, reqQuitEventListener);
        view.addEventListener(ReqSwapShelves.class, reqSwapShelvesEventListener);
        view.addEventListener(ReqTakeFromMarket.class, reqTakeFromMarketEventListener);
    }

    public void unregisterOnVC(EventDispatcher view) {
        view.removeEventListener(ReqActivateProduction.class, reqActivateProductionEventListener);
        view.removeEventListener(ReqBuyDevCard.class, reqBuyDevCardEventListener);
        view.removeEventListener(ReqChooseLeaders.class, reqChooseLeadersEventListener);
        view.removeEventListener(ReqChooseResources.class, reqChooseResourcesEventListener);
        view.removeEventListener(ReqEndTurn.class, reqEndTurnEventListener);
        view.removeEventListener(ReqJoin.class, reqJoinEventListener);
        view.removeEventListener(ReqLeaderAction.class, reqLeaderActionEventListener);
        view.removeEventListener(ReqNewGame.class, reqNewGameEventListener);
        view.removeEventListener(ReqQuit.class, reqQuitEventListener);
        view.removeEventListener(ReqSwapShelves.class, reqSwapShelvesEventListener);
        view.removeEventListener(ReqTakeFromMarket.class, reqTakeFromMarketEventListener);
    }

    public abstract void run();

    public void stop() {
        send(new ReqGoodbye());
        listening = false;
    }

    public void send(Event event) {
        if (out != null)
            out.println(protocol.processOutput(event));
    }
}
