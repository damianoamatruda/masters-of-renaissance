package it.polimi.ingsw.client;

import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;

import java.util.HashSet;
import java.util.Optional;

public abstract class UiController {
    private final Ui ui;
    protected final ViewModel vm;

    public UiController(Ui ui) {
        this.ui = ui;
        vm = ui.getViewModel();
    }

    /**
     * @return <code>true</code> if the local player's leader setup phase has concluded,
     *         <code>false</code> if it has not or if there's not enough data to know whether it has
     */
    public boolean isLocalLeaderSetupDone() {
        /* if req not accepted in a previous connection by server,
           card count to choose > 0 and cards can be discarded (hand size - count > 0) */
        return vm.getPlayerData(vm.getLocalPlayerNickname()).map(pd -> pd.getSetup().map(setup ->
                setup.hasChosenLeaders() || setup.getChosenLeadersCount() == 0 ||
                vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).size() == setup.getChosenLeadersCount()))
                .orElse(Optional.of(false))
                .get();
    }

    public boolean isLeaderSetupAvailable() {
        return !isLocalLeaderSetupDone() && vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).size() > 0;
    }

    /**
     * @return <code>true</code> if the local player's resource setup phase has concluded,
     *         <code>false</code> if it has not or if there's not enough data to know whether it has
     */
    public boolean isLocalResourceSetupDone() {
        return vm.getPlayerData(vm.getLocalPlayerNickname()).map(pd -> pd.getSetup().map(setup ->
                setup.hasChosenResources() || setup.getInitialResources() == 0))
                .orElse(Optional.of(false))
                .get();
    }

    public boolean isResourceSetupAvailable() {
        return isLocalLeaderSetupDone() && !isLocalResourceSetupDone();
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
        ui.closeClient();
    }

    public void on(UpdateAction event) {
        // TODO: IMPLEMENT IN STATES
    }

    public void on(UpdateActionToken event) {
        vm.getActionToken(event.getActionToken()).ifPresent(vm::setLatestToken);
    }

    public void on(UpdateBookedSeats event) {
    }

    public void on(UpdateCurrentPlayer event) {
        vm.setCurrentPlayer(event.getPlayer());
    }

    public void on(UpdateDevCardGrid event) {
        vm.setDevCardGrid(event.getCards());
    }

    public void on(UpdateDevSlot event) {
        vm.getPlayerData(event.getPlayer()).ifPresent(pdata ->
                pdata.setDevSlot(event.getDevSlot(), event.getDevCards()));
    }

    public void on(UpdateFaithPoints event) {
        if (event.isBlackCross())
            vm.setBlackCrossFP(event.getFaithPoints());
        else
            vm.getPlayerData(event.getPlayer()).orElseThrow().setFaithPoints(event.getFaithPoints());
    }

    public void on(UpdateGameEnd event) {
        vm.setWinner(event.getWinner());
    }

    public void on(UpdateGame event) {
        vm.setActionTokens(event.getActionTokens());
        vm.setContainers(event.getResContainers());
        vm.setDevelopmentCards(event.getDevelopmentCards());
        vm.setLeaderCards(event.getLeaderCards());
        vm.setPlayerNicknames(event.getPlayers());
        vm.setProductions(event.getProductions());
        vm.setFaithTrack(event.getFaithTrack());
        vm.setDevCardColors(event.getColors());
        vm.setResourceTypes(event.getResourceTypes());
        vm.setSetupDone(event.isSetupDone());
        vm.setSlotsCount(event.getSlotsCount());
        vm.setInkwellPlayer(event.getInkwellPlayer());
    }

    public void on(UpdateJoinGame event) {
    }

    public void on(UpdateLastRound event) {
        vm.setLastRound();
    }

    public void on(UpdateActivateLeader event) {
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

        vm.getPlayerData(event.getPlayer()).orElseThrow().setLeadersHand(new HashSet<>(event.getLeaders()));
    }

    public void on(UpdateLeadersHandCount event) {
        vm.getPlayerData(event.getPlayer()).orElseThrow().setLeadersCount(event.getLeadersCount());
    }

    public void on(UpdateMarket event) {
        vm.setMarket(event.getMarket());
    }

    public void on(UpdatePlayer event) {
        vm.setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
    }

    public void on(UpdatePlayerStatus event) {
        vm.getPlayerData(event.getPlayer()).ifPresent(pdata -> pdata.setActive(event.isActive()));
    }

    public void on(UpdateResourceContainer event) {
        vm.setContainer(event.getResContainer());
    }

    public void on(ErrServerUnavailable event) {
    }

    public void on(UpdateSetupDone event) {
        vm.setSetupDone(true);
        if (vm.getPlayerNicknames().size() > 1) {
        }
    }

    public void on(UpdateVaticanSection event) {
        vm.activateVaticanSection(event.getVaticanSection(), event.getBonusGivenPlayers());
    }

    public void on(UpdateVictoryPoints event) {
        vm.getPlayerData(event.getPlayer()).orElseThrow().setVictoryPoints(event.getVictoryPoints());
    }
}
