package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;

/** Interface defining the 'View' role in the MVC architecture. */
public abstract class View extends EventDispatcher {
    public void registerOnModelGame(EventDispatcher game) {
        game.addEventListener(ErrAction.class, this::dispatch);
        game.addEventListener(ErrActiveLeaderDiscarded.class, this::dispatch);
        game.addEventListener(ErrBuyDevCard.class, this::dispatch);
        game.addEventListener(ErrCardRequirements.class, this::dispatch);
        game.addEventListener(ErrInitialChoice.class, this::dispatch);
        game.addEventListener(ErrNewGame.class, this::dispatch);
        game.addEventListener(ErrNickname.class, this::dispatch);
        game.addEventListener(ErrObjectNotOwned.class, this::dispatch);
        game.addEventListener(ErrReplacedTransRecipe.class, this::dispatch);
        game.addEventListener(ErrReplacedTransRecipe.class, this::dispatch);
        game.addEventListener(ErrResourceReplacement.class, this::dispatch);
        game.addEventListener(ErrResourceTransfer.class, this::dispatch);
        game.addEventListener(UpdateBookedSeats.class, this::dispatch);
        game.addEventListener(UpdateJoinGame.class, this::dispatch);
        game.addEventListener(UpdateGameStart.class, this::dispatch);
        game.addEventListener(UpdateCurrentPlayer.class, this::dispatch);
        game.addEventListener(UpdateSetupDone.class, this::dispatch);
        game.addEventListener(UpdateGameResume.class, this::dispatch);
        game.addEventListener(UpdateLastRound.class, this::dispatch);
        game.addEventListener(UpdateGameEnd.class, this::dispatch);
        game.addEventListener(UpdatePlayer.class, this::dispatch);
        game.addEventListener(UpdateLeadersHandCount.class, this::dispatch);
        game.addEventListener(UpdateFaithPoints.class, this::dispatch);
        game.addEventListener(UpdateVictoryPoints.class, this::dispatch);
        game.addEventListener(UpdatePlayerStatus.class, this::dispatch);
        game.addEventListener(UpdateDevCardSlot.class, this::dispatch);
        game.addEventListener(UpdateLeader.class, this::dispatch);
        game.addEventListener(UpdateResourceContainer.class, this::dispatch);
        game.addEventListener(UpdateDevCardGrid.class, this::dispatch);
        game.addEventListener(UpdateMarket.class, this::dispatch);
        game.addEventListener(UpdateVaticanSection.class, this::dispatch);
        game.addEventListener(UpdateActionToken.class, this::dispatch);
    }

    public void unregisterOnModelGame(EventDispatcher game) {
        game.removeEventListener(ErrAction.class, this::dispatch);
        game.removeEventListener(ErrActiveLeaderDiscarded.class, this::dispatch);
        game.removeEventListener(ErrBuyDevCard.class, this::dispatch);
        game.removeEventListener(ErrCardRequirements.class, this::dispatch);
        game.removeEventListener(ErrInitialChoice.class, this::dispatch);
        game.removeEventListener(ErrNewGame.class, this::dispatch);
        game.removeEventListener(ErrNickname.class, this::dispatch);
        game.removeEventListener(ErrObjectNotOwned.class, this::dispatch);
        game.removeEventListener(ErrReplacedTransRecipe.class, this::dispatch);
        game.removeEventListener(ErrReplacedTransRecipe.class, this::dispatch);
        game.removeEventListener(ErrResourceReplacement.class, this::dispatch);
        game.removeEventListener(ErrResourceTransfer.class, this::dispatch);
        game.removeEventListener(UpdateBookedSeats.class, this::dispatch);
        game.removeEventListener(UpdateJoinGame.class, this::dispatch);
        game.removeEventListener(UpdateGameStart.class, this::dispatch);
        game.removeEventListener(UpdateCurrentPlayer.class, this::dispatch);
        game.removeEventListener(UpdateSetupDone.class, this::dispatch);
        game.removeEventListener(UpdateGameResume.class, this::dispatch);
        game.removeEventListener(UpdateLastRound.class, this::dispatch);
        game.removeEventListener(UpdateGameEnd.class, this::dispatch);
        game.removeEventListener(UpdatePlayer.class, this::dispatch);
        game.removeEventListener(UpdateLeadersHandCount.class, this::dispatch);
        game.removeEventListener(UpdateFaithPoints.class, this::dispatch);
        game.removeEventListener(UpdateVictoryPoints.class, this::dispatch);
        game.removeEventListener(UpdatePlayerStatus.class, this::dispatch);
        game.removeEventListener(UpdateDevCardSlot.class, this::dispatch);
        game.removeEventListener(UpdateLeader.class, this::dispatch);
        game.removeEventListener(UpdateResourceContainer.class, this::dispatch);
        game.removeEventListener(UpdateDevCardGrid.class, this::dispatch);
        game.removeEventListener(UpdateMarket.class, this::dispatch);
        game.removeEventListener(UpdateVaticanSection.class, this::dispatch);
        game.removeEventListener(UpdateActionToken.class, this::dispatch);
    }

    public void registerOnModelPlayer(EventDispatcher player) {
        player.addEventListener(UpdateLeadersHand.class, this::dispatch);
    }

    public void unregisterOnModelPlayer(EventDispatcher player) {
        player.removeEventListener(UpdateLeadersHand.class, this::dispatch);
    }

    public void registerOnVC(EventDispatcher view) {
        view.addEventListener(ReqQuit.class, this::dispatch);
        view.addEventListener(ReqJoin.class, this::dispatch);
        view.addEventListener(ReqNewGame.class, this::dispatch);
        view.addEventListener(ReqChooseLeaders.class, this::dispatch);
        view.addEventListener(ReqChooseResources.class, this::dispatch);
        view.addEventListener(ReqSwapShelves.class, this::dispatch);
        view.addEventListener(ReqLeaderAction.class, this::dispatch);
        view.addEventListener(ReqTakeFromMarket.class, this::dispatch);
        view.addEventListener(ReqBuyDevCard.class, this::dispatch);
        view.addEventListener(ReqActivateProduction.class, this::dispatch);
        view.addEventListener(ReqEndTurn.class, this::dispatch);
    }

    public void unregisterOnVC(EventDispatcher view) {
        view.removeEventListener(ReqQuit.class, this::dispatch);
        view.removeEventListener(ReqJoin.class, this::dispatch);
        view.removeEventListener(ReqNewGame.class, this::dispatch);
        view.removeEventListener(ReqChooseLeaders.class, this::dispatch);
        view.removeEventListener(ReqChooseResources.class, this::dispatch);
        view.removeEventListener(ReqSwapShelves.class, this::dispatch);
        view.removeEventListener(ReqLeaderAction.class, this::dispatch);
        view.removeEventListener(ReqTakeFromMarket.class, this::dispatch);
        view.removeEventListener(ReqBuyDevCard.class, this::dispatch);
        view.removeEventListener(ReqActivateProduction.class, this::dispatch);
        view.removeEventListener(ReqEndTurn.class, this::dispatch);
    }
}
