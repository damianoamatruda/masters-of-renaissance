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

    public void registerOnModelLobby(EventDispatcher lobby) {
        lobby.addEventListener(ResQuit.class, resQuitEventListener);
        lobby.addEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        lobby.addEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        lobby.addEventListener(ErrNewGame.class, errNewGameEventListener);
        lobby.addEventListener(ErrNickname.class, errNicknameEventListener);
    }

    public void unregisterOnModelLobby(EventDispatcher lobby) {
        lobby.removeEventListener(ResQuit.class, resQuitEventListener);
        lobby.removeEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        lobby.removeEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        lobby.removeEventListener(ErrNewGame.class, errNewGameEventListener);
        lobby.removeEventListener(ErrNickname.class, errNicknameEventListener);
    }

    public void registerOnModelGameContext(EventDispatcher gameContext) {
        gameContext.addEventListener(ErrAction.class, errActionEventListener);
        gameContext.addEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        gameContext.addEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        gameContext.addEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        gameContext.addEventListener(ErrInitialChoice.class, errInitialChoiceEventListener);
        gameContext.addEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        gameContext.addEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        gameContext.addEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        gameContext.addEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        gameContext.addEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        gameContext.addEventListener(UpdateAction.class, updateActionEventListener);
    }

    public void unregisterOnModelGameContext(EventDispatcher gameContext) {
        gameContext.removeEventListener(ErrAction.class, errActionEventListener);
        gameContext.removeEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        gameContext.removeEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        gameContext.removeEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        gameContext.removeEventListener(ErrInitialChoice.class, errInitialChoiceEventListener);
        gameContext.removeEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        gameContext.removeEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        gameContext.removeEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        gameContext.removeEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        gameContext.removeEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        gameContext.removeEventListener(UpdateAction.class, updateActionEventListener);
    }

    public void registerOnModelGame(EventDispatcher game) {
        game.addEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        game.addEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        game.addEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        game.addEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        game.addEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        game.addEventListener(UpdateGame.class, updateGameEventListener);
        game.addEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        game.addEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        game.addEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        game.addEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        game.addEventListener(UpdateMarket.class, updateMarketEventListener);
        game.addEventListener(UpdatePlayer.class, updatePlayerEventListener);
        game.addEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        game.addEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        game.addEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        game.addEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        game.addEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
    }

    public void unregisterOnModelGame(EventDispatcher game) {
        game.removeEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        game.removeEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        game.removeEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        game.removeEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        game.removeEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        game.removeEventListener(UpdateGame.class, updateGameEventListener);
        game.removeEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        game.removeEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        game.removeEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        game.removeEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        game.removeEventListener(UpdateMarket.class, updateMarketEventListener);
        game.removeEventListener(UpdatePlayer.class, updatePlayerEventListener);
        game.removeEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        game.removeEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        game.removeEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        game.removeEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        game.removeEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
    }

    public void registerOnModelPlayer(EventDispatcher player) {
        player.addEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
    }

    public void unregisterOnModelPlayer(EventDispatcher player) {
        player.removeEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
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
