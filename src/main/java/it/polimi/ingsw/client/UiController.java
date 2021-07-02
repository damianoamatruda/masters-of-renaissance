package it.polimi.ingsw.client;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayer;

public abstract class UiController {
    private final Ui ui;
    protected final ViewModel vm;

    public UiController(Ui ui) {
        this.ui = ui;
        this.vm = ui.getViewModel();
    }

    /**
     * @return <code>true</code> if the local player's leader setup phase has concluded,
     *         <code>false</code> if it has not or if there's not enough data to know whether it has
     */
    public boolean isLocalLeaderSetupDone() {
        /* if req not accepted in a previous connection by server,
           card count to choose > 0 and cards can be discarded (hand size - count > 0) */
        return vm.getLocalPlayer()
                .flatMap(vm::getPlayer)
                .map(ReducedPlayer::getSetup).map(setup ->
                        setup.hasChosenLeaders() || setup.getChosenLeadersCount() == 0
                                || vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().size() == setup.getChosenLeadersCount())
                .orElse(false);
    }

    public boolean isLeaderSetupAvailable() {
        return !isLocalLeaderSetupDone() && vm.getLocalPlayer().isPresent() && !vm.getPlayerLeaderCards(vm.getLocalPlayer().get()).isEmpty();
    }

    /**
     * @return <code>true</code> if the local player's resource setup phase has concluded,
     *         <code>false</code> if it has not or if there's not enough data to know whether it has
     */
    public boolean isLocalResourceSetupDone() {
        return vm.getLocalPlayer()
                .flatMap(vm::getPlayer)
                .map(ReducedPlayer::getSetup).map(setup ->
                        setup.hasChosenResources() || setup.getInitialResources() == 0)
                .orElse(false);
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

    public void on(ErrInvalidResourceTransaction event) {
    }

    public void on(ErrResourceTransfer event) {
    }

    public void on(ResQuit event) {
        ui.closeClient();
    }

    public void on(UpdateAction event) {
        /* Implemented in subclasses */
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
        vm.getPlayer(event.getPlayer()).ifPresent(player ->
                vm.putPlayer(player.setDevSlot(event.getDevSlot(), event.getDevCards())));
    }

    public void on(UpdateFaithPoints event) {
        if (event.isBlackCross())
            vm.setBlackPoints(event.getFaithPoints());
        else
            vm.getPlayer(event.getPlayer()).ifPresent(player ->
                    vm.putPlayer(player.setFaithPoints(event.getFaithPoints())));
    }

    public void on(UpdateGameEnd event) {
        vm.setGameEnded(true);
        vm.setWinnerPlayer(event.getWinner());
    }

    public void on(UpdateGame event) {
        vm.setPlayers(event.getPlayers());
        vm.setDevCardColors(event.getDevCardColors());
        vm.setResourceTypes(event.getResourceTypes());
        vm.setLeaderCards(event.getLeaderCards());
        vm.setDevelopmentCards(event.getDevelopmentCards());
        vm.setContainers(event.getResContainers());
        vm.setProductions(event.getProductions());
        vm.setActionTokens(event.getActionTokens());
        vm.setFaithTrack(event.getFaithTrack());
        vm.setMarket(event.getMarket());
        vm.setDevCardGrid(event.getDevCardGrid());
        vm.setSetupDone(event.isSetupDone());
        vm.setDevSlotsCount(event.getDevSlotsCount());
        vm.setCurrentPlayer(event.getCurrentPlayer());
        vm.setInkwellPlayer(event.getInkwellPlayer());
        vm.setWinnerPlayer(event.getWinnerPlayer());
        vm.setBlackPoints(event.getBlackPoints());
        vm.setLastRound(event.isLastRound());
        vm.setGameEnded(event.isEnded());
    }

    public void on(UpdateJoinGame event) {
    }

    public void on(UpdateLastRound event) {
        vm.setLastRound(true);
    }

    public void on(UpdateActivateLeader event) {
        vm.activateLeaderCard(event.getLeader());
    }

    public void on(UpdateLeadersHand event) {
        vm.getPlayer(event.getPlayer()).ifPresent(player ->
                vm.putPlayer(player.setLeadersHand(event.getLeaders())));
    }

    public void on(UpdateLeadersHandCount event) {
        vm.getPlayer(event.getPlayer()).ifPresent(player ->
                vm.putPlayer(player.setLeadersHandCount(event.getLeadersHandCount())));
    }

    public void on(UpdateMarket event) {
        vm.setMarket(event.getMarket());
    }

    public void on(UpdatePlayerStatus event) {
        vm.getPlayer(event.getPlayer()).ifPresent(player ->
                vm.putPlayer(player.setActive(event.isActive())));
    }

    public void on(UpdateResourceContainer event) {
        vm.setContainer(event.getResContainer());
    }

    public void on(ErrServerUnavailable event) {
    }

    public void on(UpdateSetupDone event) {
        vm.setSetupDone(true);
    }

    public void on(UpdateVaticanSection event) {
        vm.activateVaticanSection(event.getVaticanSection(), event.getBonusGivenPlayers());
    }

    public void on(UpdateVictoryPoints event) {
        vm.getPlayer(event.getPlayer()).ifPresent(player ->
                vm.putPlayer(player.setVictoryPoints(event.getVictoryPoints())));
    }
}
