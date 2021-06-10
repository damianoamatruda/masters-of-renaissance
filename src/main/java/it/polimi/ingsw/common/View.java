package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;

/**
 * This class represents the View in the MVC architecture.
 */
public class View extends EventDispatcher {
    private EventListener<ResQuit> resQuitEventListener = event -> {
    };
    private EventListener<UpdateBookedSeats> updateBookedSeatsEventListener = event -> {
    };
    private EventListener<UpdateJoinGame> updateJoinGameEventListener = event -> {
    };
    private EventListener<ErrNewGame> errNewGameEventListener = event -> {
    };
    private EventListener<ErrNickname> errNicknameEventListener = event -> {
    };
    private EventListener<ErrAction> errActionEventListener = event -> {
    };
    private EventListener<ErrActiveLeaderDiscarded> errActiveLeaderDiscardedEventListener = event -> {
    };
    private EventListener<ErrBuyDevCard> errBuyDevCardEventListener = event -> {
    };
    private EventListener<ErrCardRequirements> errCardRequirementsEventListener = event -> {
    };
    private EventListener<ErrInitialChoice> errInitialChoiceEventListener = event -> {
    };
    private EventListener<ErrNoSuchEntity> errNoSuchEntityEventListener = event -> {
    };
    private EventListener<ErrObjectNotOwned> errObjectNotOwnedEventListener = event -> {
    };
    private EventListener<ErrReplacedTransRecipe> errReplacedTransRecipeEventListener = event -> {
    };
    private EventListener<ErrResourceReplacement> errResourceReplacementEventListener = event -> {
    };
    private EventListener<ErrResourceTransfer> errResourceTransferEventListener = event -> {
    };
    private EventListener<UpdateAction> updateActionEventListener = event -> {
    };
    private EventListener<UpdateActionToken> updateActionTokenEventListener = event -> {
    };
    private EventListener<UpdateCurrentPlayer> updateCurrentPlayerEventListener = event -> {
    };
    private EventListener<UpdateDevCardGrid> updateDevCardGridEventListener = event -> {
    };
    private EventListener<UpdateDevCardSlot> updateDevCardSlotEventListener = event -> {
    };
    private EventListener<UpdateFaithPoints> updateFaithPointsEventListener = event -> {
    };
    private EventListener<UpdateGame> updateGameEventListener = event -> {
    };
    private EventListener<UpdateGameEnd> updateGameEndEventListener = event -> {
    };
    private EventListener<UpdateLastRound> updateLastRoundEventListener = event -> {
    };
    private EventListener<UpdateActivateLeader> updateActivateLeaderEventListener = event -> {
    };
    private EventListener<UpdateLeadersHandCount> updateLeadersHandCountEventListener = event -> {
    };
    private EventListener<UpdateMarket> updateMarketEventListener = event -> {
    };
    private EventListener<UpdatePlayer> updatePlayerEventListener = event -> {
    };
    private EventListener<UpdatePlayerStatus> updatePlayerStatusEventListener = event -> {
    };
    private EventListener<UpdateResourceContainer> updateResourceContainerEventListener = event -> {
    };
    private EventListener<UpdateSetupDone> updateSetupDoneEventListener = event -> {
    };
    private EventListener<UpdateVaticanSection> updateVaticanSectionEventListener = event -> {
    };
    private EventListener<UpdateVictoryPoints> updateVictoryPointsEventListener = event -> {
    };
    private EventListener<UpdateLeadersHand> updateLeadersHandEventListener = event -> {
    };

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

    public void setResQuitEventListener(EventListener<ResQuit> resQuitEventListener) {
        this.resQuitEventListener = event -> on(event, resQuitEventListener);
    }

    public void setUpdateBookedSeatsEventListener(EventListener<UpdateBookedSeats> updateBookedSeatsEventListener) {
        this.updateBookedSeatsEventListener = event -> on(event, updateBookedSeatsEventListener);
    }

    public void setUpdateJoinGameEventListener(EventListener<UpdateJoinGame> updateJoinGameEventListener) {
        this.updateJoinGameEventListener = event -> on(event, updateJoinGameEventListener);
    }

    public void setErrNewGameEventListener(EventListener<ErrNewGame> errNewGameEventListener) {
        this.errNewGameEventListener = event -> on(event, errNewGameEventListener);
    }

    public void setErrNicknameEventListener(EventListener<ErrNickname> errNicknameEventListener) {
        this.errNicknameEventListener = event -> on(event, errNicknameEventListener);
    }

    public void setErrActionEventListener(EventListener<ErrAction> errActionEventListener) {
        this.errActionEventListener = event -> on(event, errActionEventListener);
    }

    public void setErrActiveLeaderDiscardedEventListener(EventListener<ErrActiveLeaderDiscarded> errActiveLeaderDiscardedEventListener) {
        this.errActiveLeaderDiscardedEventListener = event -> on(event, errActiveLeaderDiscardedEventListener);
    }

    public void setErrBuyDevCardEventListener(EventListener<ErrBuyDevCard> errBuyDevCardEventListener) {
        this.errBuyDevCardEventListener = event -> on(event, errBuyDevCardEventListener);
    }

    public void setErrCardRequirementsEventListener(EventListener<ErrCardRequirements> errCardRequirementsEventListener) {
        this.errCardRequirementsEventListener = event -> on(event, errCardRequirementsEventListener);
    }

    public void setErrInitialChoiceEventListener(EventListener<ErrInitialChoice> errInitialChoiceEventListener) {
        this.errInitialChoiceEventListener = event -> on(event, errInitialChoiceEventListener);
    }

    public void setErrNoSuchEntityEventListener(EventListener<ErrNoSuchEntity> errNoSuchEntityEventListener) {
        this.errNoSuchEntityEventListener = event -> on(event, errNoSuchEntityEventListener);
    }

    public void setErrObjectNotOwnedEventListener(EventListener<ErrObjectNotOwned> errObjectNotOwnedEventListener) {
        this.errObjectNotOwnedEventListener = event -> on(event, errObjectNotOwnedEventListener);
    }

    public void setErrReplacedTransRecipeEventListener(EventListener<ErrReplacedTransRecipe> errReplacedTransRecipeEventListener) {
        this.errReplacedTransRecipeEventListener = event -> on(event, errReplacedTransRecipeEventListener);
    }

    public void setErrResourceReplacementEventListener(EventListener<ErrResourceReplacement> errResourceReplacementEventListener) {
        this.errResourceReplacementEventListener = event -> on(event, errResourceReplacementEventListener);
    }

    public void setErrResourceTransferEventListener(EventListener<ErrResourceTransfer> errResourceTransferEventListener) {
        this.errResourceTransferEventListener = event -> on(event, errResourceTransferEventListener);
    }

    public void setUpdateActionEventListener(EventListener<UpdateAction> updateActionEventListener) {
        this.updateActionEventListener = event -> on(event, updateActionEventListener);
    }

    public void setUpdateActionTokenEventListener(EventListener<UpdateActionToken> updateActionTokenEventListener) {
        this.updateActionTokenEventListener = event -> on(event, updateActionTokenEventListener);
    }

    public void setUpdateCurrentPlayerEventListener(EventListener<UpdateCurrentPlayer> updateCurrentPlayerEventListener) {
        this.updateCurrentPlayerEventListener = event -> on(event, updateCurrentPlayerEventListener);
    }

    public void setUpdateDevCardGridEventListener(EventListener<UpdateDevCardGrid> updateDevCardGridEventListener) {
        this.updateDevCardGridEventListener = event -> on(event, updateDevCardGridEventListener);
    }

    public void setUpdateDevCardSlotEventListener(EventListener<UpdateDevCardSlot> updateDevCardSlotEventListener) {
        this.updateDevCardSlotEventListener = event -> on(event, updateDevCardSlotEventListener);
    }

    public void setUpdateFaithPointsEventListener(EventListener<UpdateFaithPoints> updateFaithPointsEventListener) {
        this.updateFaithPointsEventListener = event -> on(event, updateFaithPointsEventListener);
    }

    public void setUpdateGameEventListener(EventListener<UpdateGame> updateGameEventListener) {
        this.updateGameEventListener = event -> on(event, updateGameEventListener);
    }

    public void setUpdateGameEndEventListener(EventListener<UpdateGameEnd> updateGameEndEventListener) {
        this.updateGameEndEventListener = event -> on(event, updateGameEndEventListener);
    }

    public void setUpdateLastRoundEventListener(EventListener<UpdateLastRound> updateLastRoundEventListener) {
        this.updateLastRoundEventListener = event -> on(event, updateLastRoundEventListener);
    }

    public void setUpdateActivateLeaderEventListener(EventListener<UpdateActivateLeader> updateActivateLeaderEventListener) {
        this.updateActivateLeaderEventListener = event -> on(event, updateActivateLeaderEventListener);
    }

    public void setUpdateLeadersHandCountEventListener(EventListener<UpdateLeadersHandCount> updateLeadersHandCountEventListener) {
        this.updateLeadersHandCountEventListener = event -> on(event, updateLeadersHandCountEventListener);
    }

    public void setUpdateMarketEventListener(EventListener<UpdateMarket> updateMarketEventListener) {
        this.updateMarketEventListener = event -> on(event, updateMarketEventListener);
    }

    public void setUpdatePlayerEventListener(EventListener<UpdatePlayer> updatePlayerEventListener) {
        this.updatePlayerEventListener = event -> on(event, updatePlayerEventListener);
    }

    public void setUpdatePlayerStatusEventListener(EventListener<UpdatePlayerStatus> updatePlayerStatusEventListener) {
        this.updatePlayerStatusEventListener = event -> on(event, updatePlayerStatusEventListener);
    }

    public void setUpdateResourceContainerEventListener(EventListener<UpdateResourceContainer> updateResourceContainerEventListener) {
        this.updateResourceContainerEventListener = event -> on(event, updateResourceContainerEventListener);
    }

    public void setUpdateSetupDoneEventListener(EventListener<UpdateSetupDone> updateSetupDoneEventListener) {
        this.updateSetupDoneEventListener = event -> on(event, updateSetupDoneEventListener);
    }

    public void setUpdateVaticanSectionEventListener(EventListener<UpdateVaticanSection> updateVaticanSectionEventListener) {
        this.updateVaticanSectionEventListener = event -> on(event, updateVaticanSectionEventListener);
    }

    public void setUpdateVictoryPointsEventListener(EventListener<UpdateVictoryPoints> updateVictoryPointsEventListener) {
        this.updateVictoryPointsEventListener = event -> on(event, updateVictoryPointsEventListener);
    }

    public void setUpdateLeadersHandEventListener(EventListener<UpdateLeadersHand> updateLeadersHandEventListener) {
        this.updateLeadersHandEventListener = event -> on(event, updateLeadersHandEventListener);
    }

    private <T extends ViewEvent> void on(T viewEvent, EventListener<T> listener) {
        // TODO: Add logger
        System.out.printf("%s, isPresent: %s, equals: %s%n", viewEvent, viewEvent.getView().isPresent(), viewEvent.getView().isPresent() && viewEvent.getView().get().equals(this));
        if (viewEvent.getView().isPresent() && !viewEvent.getView().get().equals(this))
            return;
        listener.on(viewEvent);
    }

    private <T extends Event> void on(T event, EventListener<T> listener) {
        listener.on(event);
    }
}
