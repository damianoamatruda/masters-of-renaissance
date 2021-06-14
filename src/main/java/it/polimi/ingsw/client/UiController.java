package it.polimi.ingsw.client;

import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;

public abstract class UiController {
    private final Ui ui;

    public UiController(Ui ui) {
        this.ui = ui;
    }

    public void on(ErrAction event) {
    }

    public void on(ErrActiveLeaderDiscarded event) {
    }

    public void on(ErrBuyDevCard event) {
    }

    public void on(ErrCardRequirements event) {
    }

    public void on(ErrInitialChoice event) {
    }

    public void on(ErrNewGame event) {
    }

    public void on(ErrNickname event) {
    }

    public void on(ErrNoSuchEntity event) {
    }

    public void on(ErrObjectNotOwned event) {
    }

    public void on(ErrReplacedTransRecipe event) {
    }

    public void on(ErrResourceReplacement event) {
    }

    public void on(ErrResourceTransfer event) {
    }

    public void on(ResQuit event) {
    }

    public void on(UpdateAction event) {
        // TODO: IMPLEMENT IN STATES
    }

    public void on(UpdateActionToken event) {
    }

    public void on(UpdateBookedSeats event) {
    }

    public void on(UpdateCurrentPlayer event) {
        ui.getViewModel().setCurrentPlayer(event.getPlayer());
    }

    public void on(UpdateDevCardGrid event) {
        ui.getViewModel().setDevCardGrid(event.getCards());
    }

    public void on(UpdateDevCardSlot event) {
        ui.getViewModel().getCurrentPlayerData().orElseThrow().pushToDevSlot(event.getDevSlot(), event.getDevCard());
    }

    public void on(UpdateFaithPoints event) {
        ViewModel vm = ui.getViewModel();

        if (event.isBlackCross())
            vm.setBlackCrossFP(event.getFaithPoints());
        else
            vm.getPlayerData(event.getPlayer()).orElseThrow().setFaithPoints(event.getFaithPoints());
    }

    public void on(UpdateGameEnd event) {
        ui.getViewModel().setWinner(event.getWinner());
    }

    public void on(UpdateGame event) {
        ViewModel vm = ui.getViewModel();

        vm.setActionTokens(event.getActionTokens());
        vm.setContainers(event.getResContainers());
        vm.setDevelopmentCards(event.getDevelopmentCards());
        vm.setLeaderCards(event.getLeaderCards());
        vm.setPlayerNicknames(event.getPlayers());
        vm.setProductions(event.getProductions());
        vm.setFaithTrack(event.getFaithTrack());
        vm.setDevCardColors(event.getColors());
        vm.setResourceTypes(event.getResourceTypes());
        vm.setResumedGame(event.isResumed());
        vm.setSlotsCount(event.getSlotsCount());
    }

    public void on(UpdateJoinGame event) {
    }

    public void on(UpdateLastRound event) {
        ui.getViewModel().setLastRound();
    }

    public void on(UpdateActivateLeader event) {
        ViewModel vm = ui.getViewModel();

        vm.activateLeaderCard(event.getLeader());
    }

    public void on(UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategame
            currplayer
            market
            devcardgrid
            player
            leadershand -> client has enough info for leader choice */

        ui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersHand(event.getLeaders());
    }

    public void on(UpdateLeadersHandCount event) {
        ui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersCount(event.getLeadersCount());
    }

    public void on(UpdateMarket event) {
        ui.getViewModel().setMarket(event.getMarket());
    }

    public void on(UpdatePlayer event) {
        ViewModel vm = ui.getViewModel();
        vm.setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
        // TODO put baseProd outside of PlayerData, since it is unique (?)
    }

    public void on(UpdatePlayerStatus event) {
        ui.getViewModel().getPlayerData(event.getPlayer()).ifPresent(pdata -> pdata.setActive(event.isActive()));
    }

    public void on(UpdateResourceContainer event) {
        ui.getViewModel().setContainer(event.getResContainer());
    }

    public void on(UpdateSetupDone event) {
        ui.getViewModel().setSetupDone(true);
        if (ui.getViewModel().getPlayerNicknames().size() > 1) {
        }
    }

    public void on(UpdateVaticanSection event) {
        ui.getViewModel().setVaticanSection(event.getVaticanSection());
    }

    public void on(UpdateVictoryPoints event) {
        ui.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setVictoryPoints(event.getVictoryPoints());
    }
}
