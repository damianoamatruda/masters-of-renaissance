package it.polimi.ingsw.client;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class ClientController {
    private ReducedGame model;

    public ClientController(ReducedGame model) {
        this.model = model;
    }

    public void registerToView(View view) {
        view.addEventListener(ErrAction.class, event -> on(view, event));
        view.addEventListener(ErrActiveLeaderDiscarded.class, event -> on(view, event));
        view.addEventListener(ErrBuyDevCard.class, event -> on(view, event));
        view.addEventListener(ErrCardRequirements.class, event -> on(view, event));
        view.addEventListener(ErrInitialChoice.class, event -> on(view, event));
        view.addEventListener(ErrNewGame.class, event -> on(view, event));
        view.addEventListener(ErrNickname.class, event -> on(view, event));
        view.addEventListener(ErrObjectNotOwned.class, event -> on(view, event));
        view.addEventListener(ErrReplacedTransRecipe.class, event -> on(view, event));
        view.addEventListener(ErrResourceReplacement.class, event -> on(view, event));
        view.addEventListener(ErrResourceTransfer.class, event -> on(view, event));
        view.addEventListener(ErrProtocol.class, event -> on(view, event));
        view.addEventListener(ErrRuntime.class, event -> on(view, event));
        view.addEventListener(ResGoodbye.class, event -> on(view, event));
        view.addEventListener(ResWelcome.class, event -> on(view, event));
        view.addEventListener(UpdateActionToken.class, event -> on(view, event));
        view.addEventListener(UpdateBookedSeats.class, event -> on(view, event));
        view.addEventListener(UpdateCurrentPlayer.class, event -> on(view, event));
        view.addEventListener(UpdateDevCardGrid.class, event -> on(view, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> on(view, event));
        view.addEventListener(UpdateFaithPoints.class, event -> on(view, event));
        view.addEventListener(UpdateGameEnd.class, event -> on(view, event));
        view.addEventListener(UpdateGameResume.class, event -> on(view, event));
        view.addEventListener(UpdateGameStart.class, event -> on(view, event));
        view.addEventListener(UpdateJoinGame.class, event -> on(view, event));
        view.addEventListener(UpdateLastRound.class, event -> on(view, event));
        view.addEventListener(UpdateLeader.class, event -> on(view, event));
        view.addEventListener(UpdateLeadersHand.class, event -> on(view, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> on(view, event));
        view.addEventListener(UpdateMarket.class, event -> on(view, event));
        view.addEventListener(UpdatePlayer.class, event -> on(view, event));
        view.addEventListener(UpdatePlayerStatus.class, event -> on(view, event));
        view.addEventListener(UpdateResourceContainer.class, event -> on(view, event));
        view.addEventListener(UpdateSetupDone.class, event -> on(view, event));
        view.addEventListener(UpdateVaticanSection.class, event -> on(view, event));
        view.addEventListener(UpdateVictoryPoints.class, event -> on(view, event));
    }

    public void unregisterToView(View view) {
        view.removeEventListener(ErrAction.class, event -> on(view, event));
        view.removeEventListener(ErrActiveLeaderDiscarded.class, event -> on(view, event));
        view.removeEventListener(ErrBuyDevCard.class, event -> on(view, event));
        view.removeEventListener(ErrCardRequirements.class, event -> on(view, event));
        view.removeEventListener(ErrInitialChoice.class, event -> on(view, event));
        view.removeEventListener(ErrNewGame.class, event -> on(view, event));
        view.removeEventListener(ErrNickname.class, event -> on(view, event));
        view.removeEventListener(ErrObjectNotOwned.class, event -> on(view, event));
        view.removeEventListener(ErrReplacedTransRecipe.class, event -> on(view, event));
        view.removeEventListener(ErrResourceReplacement.class, event -> on(view, event));
        view.removeEventListener(ErrResourceTransfer.class, event -> on(view, event));
        view.removeEventListener(ErrProtocol.class, event -> on(view, event));
        view.removeEventListener(ErrRuntime.class, event -> on(view, event));
        view.removeEventListener(ResGoodbye.class, event -> on(view, event));
        view.removeEventListener(ResWelcome.class, event -> on(view, event));
        view.removeEventListener(UpdateActionToken.class, event -> on(view, event));
        view.removeEventListener(UpdateBookedSeats.class, event -> on(view, event));
        view.removeEventListener(UpdateCurrentPlayer.class, event -> on(view, event));
        view.removeEventListener(UpdateDevCardGrid.class, event -> on(view, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> on(view, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> on(view, event));
        view.removeEventListener(UpdateGameEnd.class, event -> on(view, event));
        view.removeEventListener(UpdateGameResume.class, event -> on(view, event));
        view.removeEventListener(UpdateGameStart.class, event -> on(view, event));
        view.removeEventListener(UpdateJoinGame.class, event -> on(view, event));
        view.removeEventListener(UpdateLastRound.class, event -> on(view, event));
        view.removeEventListener(UpdateLeader.class, event -> on(view, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> on(view, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> on(view, event));
        view.removeEventListener(UpdateMarket.class, event -> on(view, event));
        view.removeEventListener(UpdatePlayer.class, event -> on(view, event));
        view.removeEventListener(UpdatePlayerStatus.class, event -> on(view, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> on(view, event));
        view.removeEventListener(UpdateSetupDone.class, event -> on(view, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> on(view, event));
        view.removeEventListener(UpdateVictoryPoints.class, event -> on(view, event));
    }

    // TODO show commands (don't use "on" functions?) and redirect to cache

    private void on(View view, ErrAction event) {
        
    }

    private void on(View view, ErrActiveLeaderDiscarded event) {
        
    }

    private void on(View view, ErrBuyDevCard event) {
        
    }

    private void on(View view, ErrCardRequirements event) {
        
    }

    private void on(View view, ErrInitialChoice event) {
        
    }

    private void on(View view, ErrNewGame event) {
        
    }

    private void on(View view, ErrNickname event) {
        
    }

    private void on(View view, ErrObjectNotOwned event) {
        
    }

    private void on(View view, ErrReplacedTransRecipe event) {
        
    }

    private void on(View view, ErrResourceReplacement event) {
        
    }

    private void on(View view, ErrResourceTransfer event) {
        
    }

    private void on(View view, ErrProtocol event) {
        
    }

    private void on(View view, ErrRuntime event) {
        
    }

    private void on(View view, ResGoodbye event) {
        
    }

    private void on(View view, ResWelcome event) {
        
    }

    private void on(View view, UpdateActionToken event) {
        
    }

    private void on(View view, UpdateBookedSeats event) {
        
    }

    private void on(View view, UpdateCurrentPlayer event) {
        
    }

    private void on(View view, UpdateDevCardGrid event) {
        
    }

    private void on(View view, UpdateDevCardSlot event) {
        
    }

    private void on(View view, UpdateFaithPoints event) {
        
    }

    private void on(View view, UpdateGameEnd event) {
        
    }

    private void on(View view, UpdateGameResume event) {
        
    }

    private void on(View view, UpdateGameStart event) {
        
    }

    private void on(View view, UpdateJoinGame event) {
        
    }

    private void on(View view, UpdateLastRound event) {
        
    }

    private void on(View view, UpdateLeader event) {
        
    }

    private void on(View view, UpdateLeadersHand event) {
        
    }

    private void on(View view, UpdateLeadersHandCount event) {
        
    }

    private void on(View view, UpdateMarket event) {
        model.setMarket(event.getMarket());
    }

    private void on(View view, UpdatePlayer event) {
        
    }

    private void on(View view, UpdatePlayerStatus event) {
        
    }

    private void on(View view, UpdateResourceContainer event) {
        model.setContainer(event.getResContainer());
    }

    private void on(View view, UpdateSetupDone event) {
        
    }

    private void on(View view, UpdateVaticanSection event) {
        
    }

    private void on(View view, UpdateVictoryPoints event) {
        model.setVictoryPoints(event.getVictoryPoints());
    }

}
