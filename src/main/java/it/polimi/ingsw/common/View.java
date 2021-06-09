package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;

/** Interface defining the 'View' role in the MVC architecture. */
public class View extends EventDispatcher {
    private final EventListener<ResQuit> resQuitEventListener = this::on;
    private final EventListener<UpdateBookedSeats> updateBookedSeatsEventListener = this::on;
    private final EventListener<UpdateJoinGame> updateJoinGameEventListener = this::on;
    private final EventListener<ErrNewGame> errNewGameEventListener = this::on;
    private final EventListener<ErrNickname> errNicknameEventListener = this::on;
    private final EventListener<ErrAction> errActionEventListener = this::on;
    private final EventListener<ErrActiveLeaderDiscarded> errActiveLeaderDiscardedEventListener = this::on;
    private final EventListener<ErrBuyDevCard> errBuyDevCardEventListener = this::on;
    private final EventListener<ErrCardRequirements> errCardRequirementsEventListener = this::on;
    private final EventListener<ErrInitialChoice> errInitialChoiceEventListener = this::on;
    private final EventListener<ErrNoSuchEntity> errNoSuchEntityEventListener = this::on;
    private final EventListener<ErrObjectNotOwned> errObjectNotOwnedEventListener = this::on;
    private final EventListener<ErrReplacedTransRecipe> errReplacedTransRecipeEventListener = this::on;
    private final EventListener<ErrResourceReplacement> errResourceReplacementEventListener = this::on;
    private final EventListener<ErrResourceTransfer> errResourceTransferEventListener = this::on;
    private final EventListener<UpdateAction> updateActionEventListener = this::on;
    private final EventListener<UpdateActionToken> updateActionTokenEventListener = this::on;
    private final EventListener<UpdateCurrentPlayer> updateCurrentPlayerEventListener = this::on;
    private final EventListener<UpdateDevCardGrid> updateDevCardGridEventListener = this::on;
    private final EventListener<UpdateDevCardSlot> updateDevCardSlotEventListener = this::on;
    private final EventListener<UpdateFaithPoints> updateFaithPointsEventListener = this::on;
    private final EventListener<UpdateGame> updateGameEventListener = this::on;
    private final EventListener<UpdateGameEnd> updateGameEndEventListener = this::on;
    private final EventListener<UpdateLastRound> updateLastRoundEventListener = this::on;
    private final EventListener<UpdateActivateLeader> updateActivateLeaderEventListener = this::on;
    private final EventListener<UpdateLeadersHandCount> updateLeadersHandCountEventListener = this::on;
    private final EventListener<UpdateMarket> updateMarketEventListener = this::on;
    private final EventListener<UpdatePlayer> updatePlayerEventListener = this::on;
    private final EventListener<UpdatePlayerStatus> updatePlayerStatusEventListener = this::on;
    private final EventListener<UpdateResourceContainer> updateResourceContainerEventListener = this::on;
    private final EventListener<UpdateSetupDone> updateSetupDoneEventListener = this::on;
    private final EventListener<UpdateVaticanSection> updateVaticanSectionEventListener = this::on;
    private final EventListener<UpdateVictoryPoints> updateVictoryPointsEventListener = this::on;
    private final EventListener<UpdateLeadersHand> updateLeadersHandEventListener = this::on;
    private final EventListener<ReqActivateProduction> reqActivateProductionEventListener = this::on;
    private final EventListener<ReqBuyDevCard> reqBuyDevCardEventListener = this::on;
    private final EventListener<ReqChooseLeaders> reqChooseLeadersEventListener = this::on;
    private final EventListener<ReqChooseResources> reqChooseResourcesEventListener = this::on;
    private final EventListener<ReqEndTurn> reqEndTurnEventListener = this::on;
    private final EventListener<ReqJoin> reqJoinEventListener = this::on;
    private final EventListener<ReqLeaderAction> reqLeaderActionEventListener = this::on;
    private final EventListener<ReqNewGame> reqNewGameEventListener = this::on;
    private final EventListener<ReqQuit> reqQuitEventListener = this::on;
    private final EventListener<ReqSwapShelves> reqSwapShelvesEventListener = this::on;
    private final EventListener<ReqTakeFromMarket> reqTakeFromMarketEventListener = this::on;

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

    private void on(ViewEvent viewEvent) {
        // TODO: Add logger
        System.out.printf("%s, isPresent: %s, equals: %s%n", viewEvent, viewEvent.getView().isPresent(), viewEvent.getView().isPresent() && viewEvent.getView().get().equals(this));
        if (viewEvent.getView().isPresent() && !viewEvent.getView().get().equals(this))
            return;
        dispatch(viewEvent);
    }

    private void on(Event event) {
        dispatch(event);
    }
}
