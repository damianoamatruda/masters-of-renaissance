package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;

import java.util.logging.Logger;

/**
 * This class represents the View in the MVC architecture.
 */
public class View extends AsynchronousEventDispatcher {
    private static final Logger LOGGER = Logger.getLogger(View.class.getName());

    private EventListener<ErrNewGame> errNewGameEventListener = event -> {
    };
    private EventListener<ErrNickname> errNicknameEventListener = event -> {
    };
    private EventListener<ResQuit> resQuitEventListener = event -> {
    };
    private EventListener<UpdateBookedSeats> updateBookedSeatsEventListener = event -> {
    };
    private EventListener<UpdateJoinGame> updateJoinGameEventListener = event -> {
    };
    private EventListener<ErrServerUnavailable> updateServerUnavailableEventListener = event -> {
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
    private EventListener<UpdateDevSlot> updateDevSlotEventListener = event -> {
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
        lobby.addEventListener(ErrNewGame.class, errNewGameEventListener);
        lobby.addEventListener(ErrNickname.class, errNicknameEventListener);
        lobby.addEventListener(ResQuit.class, resQuitEventListener);
        lobby.addEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        lobby.addEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        lobby.addEventListener(ErrServerUnavailable.class, updateServerUnavailableEventListener);
    }

    public void unregisterOnModelLobby(EventDispatcher lobby) {
        lobby.removeEventListener(ErrNewGame.class, errNewGameEventListener);
        lobby.removeEventListener(ErrNickname.class, errNicknameEventListener);
        lobby.removeEventListener(ResQuit.class, resQuitEventListener);
        lobby.removeEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        lobby.removeEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
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
        gameContext.addEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        gameContext.addEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        gameContext.addEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        gameContext.addEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        gameContext.addEventListener(UpdateDevSlot.class, updateDevSlotEventListener);
        gameContext.addEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        gameContext.addEventListener(UpdateGame.class, updateGameEventListener);
        gameContext.addEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        gameContext.addEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        gameContext.addEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
        gameContext.addEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        gameContext.addEventListener(UpdateMarket.class, updateMarketEventListener);
        gameContext.addEventListener(UpdatePlayer.class, updatePlayerEventListener);
        gameContext.addEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        gameContext.addEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        gameContext.addEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        gameContext.addEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        gameContext.addEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
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
        gameContext.removeEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        gameContext.removeEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        gameContext.removeEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        gameContext.removeEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        gameContext.removeEventListener(UpdateDevSlot.class, updateDevSlotEventListener);
        gameContext.removeEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        gameContext.removeEventListener(UpdateGame.class, updateGameEventListener);
        gameContext.removeEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        gameContext.removeEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        gameContext.removeEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
        gameContext.removeEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        gameContext.removeEventListener(UpdateMarket.class, updateMarketEventListener);
        gameContext.removeEventListener(UpdatePlayer.class, updatePlayerEventListener);
        gameContext.removeEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        gameContext.removeEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        gameContext.removeEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        gameContext.removeEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        gameContext.removeEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
    }

    public void setErrNewGameEventListener(EventListener<ErrNewGame> errNewGameEventListener) {
        this.errNewGameEventListener = event -> on(event, errNewGameEventListener);
    }

    public void setErrNicknameEventListener(EventListener<ErrNickname> errNicknameEventListener) {
        this.errNicknameEventListener = event -> on(event, errNicknameEventListener);
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

    public void setUpdateServerUnavailableEventListener(EventListener<ErrServerUnavailable> updateServerUnavailableEventListener) {
        this.updateServerUnavailableEventListener = event -> on(event, updateServerUnavailableEventListener);
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

    public void setUpdateDevSlotEventListener(EventListener<UpdateDevSlot> updateDevSlotEventListener) {
        this.updateDevSlotEventListener = event -> on(event, updateDevSlotEventListener);
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
        if (viewEvent.getView().isPresent() && !viewEvent.getView().get().equals(this))
            return;
        listener.on(viewEvent);
    }

    private <T extends Event> void on(T event, EventListener<T> listener) {
        listener.on(event);
    }
}
