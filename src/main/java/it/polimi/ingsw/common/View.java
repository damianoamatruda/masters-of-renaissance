package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;

/** Interface defining the 'View' role in the MVC architecture. */
public class View extends EventDispatcher {
    public void registerOnModelLobby(EventDispatcher lobby) {
        lobby.addEventListener(ResQuit.class, this::on);
        lobby.addEventListener(UpdateBookedSeats.class, this::on);
        lobby.addEventListener(UpdateJoinGame.class, this::on);
        lobby.addEventListener(ErrNewGame.class, this::on);
        lobby.addEventListener(ErrNickname.class, this::on);
    }

    public void unregisterOnModelLobby(EventDispatcher lobby) {
        lobby.removeEventListener(ResQuit.class, this::on);
        lobby.removeEventListener(UpdateBookedSeats.class, this::on);
        lobby.removeEventListener(UpdateJoinGame.class, this::on);
        lobby.removeEventListener(ErrNewGame.class, this::on);
        lobby.removeEventListener(ErrNickname.class, this::on);
    }

    public void registerOnModelGameContext(EventDispatcher gameContext) {
        gameContext.addEventListener(ErrAction.class, this::on);
        gameContext.addEventListener(ErrActiveLeaderDiscarded.class, this::on);
        gameContext.addEventListener(ErrBuyDevCard.class, this::on);
        gameContext.addEventListener(ErrCardRequirements.class, this::on);
        gameContext.addEventListener(ErrInitialChoice.class, this::on);
        gameContext.addEventListener(ErrNoSuchEntity.class, this::on);
        gameContext.addEventListener(ErrObjectNotOwned.class, this::on);
        gameContext.addEventListener(ErrReplacedTransRecipe.class, this::on);
        gameContext.addEventListener(ErrResourceReplacement.class, this::on);
        gameContext.addEventListener(ErrResourceTransfer.class, this::on);
        gameContext.addEventListener(UpdateAction.class, this::on);
    }

    public void unregisterOnModelGameContext(EventDispatcher gameContext) {
        gameContext.removeEventListener(ErrAction.class, this::on);
        gameContext.removeEventListener(ErrActiveLeaderDiscarded.class, this::on);
        gameContext.removeEventListener(ErrBuyDevCard.class, this::on);
        gameContext.removeEventListener(ErrCardRequirements.class, this::on);
        gameContext.removeEventListener(ErrInitialChoice.class, this::on);
        gameContext.removeEventListener(ErrNoSuchEntity.class, this::on);
        gameContext.removeEventListener(ErrObjectNotOwned.class, this::on);
        gameContext.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        gameContext.removeEventListener(ErrResourceReplacement.class, this::on);
        gameContext.removeEventListener(ErrResourceTransfer.class, this::on);
        gameContext.removeEventListener(UpdateAction.class, this::on);
    }

    public void registerOnModelGame(EventDispatcher game) {
        game.addEventListener(UpdateActionToken.class, this::on);
        game.addEventListener(UpdateCurrentPlayer.class, this::on);
        game.addEventListener(UpdateDevCardGrid.class, this::on);
        game.addEventListener(UpdateDevCardSlot.class, this::on);
        game.addEventListener(UpdateFaithPoints.class, this::on);
        game.addEventListener(UpdateGame.class, this::on);
        game.addEventListener(UpdateGameEnd.class, this::on);
        game.addEventListener(UpdateLastRound.class, this::on);
        game.addEventListener(UpdateLeader.class, this::on);
        game.addEventListener(UpdateLeadersHandCount.class, this::on);
        game.addEventListener(UpdateMarket.class, this::on);
        game.addEventListener(UpdatePlayer.class, this::on);
        game.addEventListener(UpdatePlayerStatus.class, this::on);
        game.addEventListener(UpdateResourceContainer.class, this::on);
        game.addEventListener(UpdateSetupDone.class, this::on);
        game.addEventListener(UpdateVaticanSection.class, this::on);
        game.addEventListener(UpdateVictoryPoints.class, this::on);
    }

    public void unregisterOnModelGame(EventDispatcher game) {
        game.removeEventListener(UpdateActionToken.class, this::on);
        game.removeEventListener(UpdateCurrentPlayer.class, this::on);
        game.removeEventListener(UpdateDevCardGrid.class, this::on);
        game.removeEventListener(UpdateDevCardSlot.class, this::on);
        game.removeEventListener(UpdateFaithPoints.class, this::on);
        game.removeEventListener(UpdateGame.class, this::on);
        game.removeEventListener(UpdateGameEnd.class, this::on);
        game.removeEventListener(UpdateLastRound.class, this::on);
        game.removeEventListener(UpdateLeader.class, this::on);
        game.removeEventListener(UpdateLeadersHandCount.class, this::on);
        game.removeEventListener(UpdateMarket.class, this::on);
        game.removeEventListener(UpdatePlayer.class, this::on);
        game.removeEventListener(UpdatePlayerStatus.class, this::on);
        game.removeEventListener(UpdateResourceContainer.class, this::on);
        game.removeEventListener(UpdateSetupDone.class, this::on);
        game.removeEventListener(UpdateVaticanSection.class, this::on);
        game.removeEventListener(UpdateVictoryPoints.class, this::on);
    }

    public void registerOnModelPlayer(EventDispatcher player) {
        player.addEventListener(UpdateLeadersHand.class, this::on);
    }

    public void unregisterOnModelPlayer(EventDispatcher player) {
        player.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    public void registerOnVC(EventDispatcher view) {
        view.addEventListener(ReqActivateProduction.class, this::on);
        view.addEventListener(ReqBuyDevCard.class, this::on);
        view.addEventListener(ReqChooseLeaders.class, this::on);
        view.addEventListener(ReqChooseResources.class, this::on);
        view.addEventListener(ReqEndTurn.class, this::on);
        view.addEventListener(ReqJoin.class, this::on);
        view.addEventListener(ReqLeaderAction.class, this::on);
        view.addEventListener(ReqNewGame.class, this::on);
        view.addEventListener(ReqQuit.class, this::on);
        view.addEventListener(ReqSwapShelves.class, this::on);
        view.addEventListener(ReqTakeFromMarket.class, this::on);
    }

    public void unregisterOnVC(EventDispatcher view) {
        view.removeEventListener(ReqActivateProduction.class, this::on);
        view.removeEventListener(ReqBuyDevCard.class, this::on);
        view.removeEventListener(ReqChooseLeaders.class, this::on);
        view.removeEventListener(ReqChooseResources.class, this::on);
        view.removeEventListener(ReqEndTurn.class, this::on);
        view.removeEventListener(ReqJoin.class, this::on);
        view.removeEventListener(ReqLeaderAction.class, this::on);
        view.removeEventListener(ReqNewGame.class, this::on);
        view.removeEventListener(ReqQuit.class, this::on);
        view.removeEventListener(ReqSwapShelves.class, this::on);
        view.removeEventListener(ReqTakeFromMarket.class, this::on);
    }

    private void on(ViewEvent viewEvent) {
        if (viewEvent.getView().isPresent() && !viewEvent.getView().get().equals(this))
            return;
        viewEvent.setView(null);
        dispatch(viewEvent);
    }

    private void on(Event event) {
        dispatch(event);
    }
}
