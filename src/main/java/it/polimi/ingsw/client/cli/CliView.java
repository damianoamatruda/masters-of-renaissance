package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.VCEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class CliView extends View implements EventListener<VCEvent> {
    private final ReducedGame cache;
    private final Cli cli;
    private VCEvent lastReq;

    /**
     * Class constructor.
     */
    public CliView(ReducedGame cache, Cli cli) {
        this.cache = cache;
        this.cli = cli;

        this.eventPasser = null;
    }

    @Override
    public void registerToModelGame(EventDispatcher dispatcher) {
        dispatcher.addEventListener(ErrAction.class, this::on);
        dispatcher.addEventListener(ErrActiveLeaderDiscarded.class, this::on);
        dispatcher.addEventListener(ErrBuyDevCard.class, this::on);
        dispatcher.addEventListener(ErrCardRequirements.class, this::on);
        dispatcher.addEventListener(ErrInitialChoice.class, this::on);
        dispatcher.addEventListener(ErrNewGame.class, this::on);
        dispatcher.addEventListener(ErrNickname.class, this::on);
        dispatcher.addEventListener(ErrObjectNotOwned.class, this::on);
        dispatcher.addEventListener(ErrReplacedTransRecipe.class, this::on);
        dispatcher.addEventListener(ErrResourceReplacement.class, this::on);
        dispatcher.addEventListener(ErrResourceTransfer.class, this::on);
        dispatcher.addEventListener(ErrProtocol.class, this::on);
        dispatcher.addEventListener(ErrRuntime.class, this::on);
        // dispatcher.addEventListener(ReqHeartbeat.class, this::on);
        dispatcher.addEventListener(ResGoodbye.class, this::on);
        dispatcher.addEventListener(ResWelcome.class, this::on);
        dispatcher.addEventListener(UpdateActionToken.class, this::on);
        dispatcher.addEventListener(UpdateBookedSeats.class, this::on);
        dispatcher.addEventListener(UpdateCurrentPlayer.class, this::on);
        dispatcher.addEventListener(UpdateDevCardGrid.class, this::on);
        dispatcher.addEventListener(UpdateDevCardSlot.class, this::on);
        dispatcher.addEventListener(UpdateFaithPoints.class, this::on);
        dispatcher.addEventListener(UpdateGameEnd.class, this::on);
        dispatcher.addEventListener(UpdateGameResume.class, this::on);
        dispatcher.addEventListener(UpdateGameStart.class, this::on);
        dispatcher.addEventListener(UpdateJoinGame.class, this::on);
        dispatcher.addEventListener(UpdateLastRound.class, this::on);
        dispatcher.addEventListener(UpdateLeader.class, this::on);
        dispatcher.addEventListener(UpdateLeadersHand.class, this::on);
        dispatcher.addEventListener(UpdateLeadersHandCount.class, this::on);
        dispatcher.addEventListener(UpdateMarket.class, this::on);
        dispatcher.addEventListener(UpdatePlayer.class, this::on);
        dispatcher.addEventListener(UpdatePlayerStatus.class, this::on);
        dispatcher.addEventListener(UpdateResourceContainer.class, this::on);
        dispatcher.addEventListener(UpdateSetupDone.class, this::on);
        dispatcher.addEventListener(UpdateVaticanSection.class, this::on);
        dispatcher.addEventListener(UpdateVictoryPoints.class, this::on);
    }

    @Override
    public void unregisterToModelGame(EventDispatcher dispatcher) {
        dispatcher.removeEventListener(ErrAction.class, this::on);
        dispatcher.removeEventListener(ErrActiveLeaderDiscarded.class, this::on);
        dispatcher.removeEventListener(ErrBuyDevCard.class, this::on);
        dispatcher.removeEventListener(ErrCardRequirements.class, this::on);
        dispatcher.removeEventListener(ErrInitialChoice.class, this::on);
        dispatcher.removeEventListener(ErrNewGame.class, this::on);
        dispatcher.removeEventListener(ErrNickname.class, this::on);
        dispatcher.removeEventListener(ErrObjectNotOwned.class, this::on);
        dispatcher.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        dispatcher.removeEventListener(ErrResourceReplacement.class, this::on);
        dispatcher.removeEventListener(ErrResourceTransfer.class, this::on);
        dispatcher.removeEventListener(ErrProtocol.class, this::on);
        dispatcher.removeEventListener(ErrRuntime.class, this::on);
        // dispatcher.removeEventListener(ReqHeartbeat.class, this::on);
        dispatcher.removeEventListener(ResGoodbye.class, this::on);
        dispatcher.removeEventListener(ResWelcome.class, this::on);
        dispatcher.removeEventListener(UpdateActionToken.class, this::on);
        dispatcher.removeEventListener(UpdateBookedSeats.class, this::on);
        dispatcher.removeEventListener(UpdateCurrentPlayer.class, this::on);
        dispatcher.removeEventListener(UpdateDevCardGrid.class, this::on);
        dispatcher.removeEventListener(UpdateDevCardSlot.class, this::on);
        dispatcher.removeEventListener(UpdateFaithPoints.class, this::on);
        dispatcher.removeEventListener(UpdateGameEnd.class, this::on);
        dispatcher.removeEventListener(UpdateGameResume.class, this::on);
        dispatcher.removeEventListener(UpdateGameStart.class, this::on);
        dispatcher.removeEventListener(UpdateJoinGame.class, this::on);
        dispatcher.removeEventListener(UpdateLastRound.class, this::on);
        dispatcher.removeEventListener(UpdateLeader.class, this::on);
        dispatcher.removeEventListener(UpdateLeadersHand.class, this::on);
        dispatcher.removeEventListener(UpdateLeadersHandCount.class, this::on);
        dispatcher.removeEventListener(UpdateMarket.class, this::on);
        dispatcher.removeEventListener(UpdatePlayer.class, this::on);
        dispatcher.removeEventListener(UpdatePlayerStatus.class, this::on);
        dispatcher.removeEventListener(UpdateResourceContainer.class, this::on);
        dispatcher.removeEventListener(UpdateSetupDone.class, this::on);
        dispatcher.removeEventListener(UpdateVaticanSection.class, this::on);
        dispatcher.removeEventListener(UpdateVictoryPoints.class, this::on);
    }

    @Override
    public void registerToModelPlayer(EventDispatcher player) {
        player.addEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void unregisterToModelPlayer(EventDispatcher player) {
        player.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    @Override
    public void on(MVEvent event) {
        throw new RuntimeException("You shouldn't be here. 'Here' is ClientView.on(MVEvent).");
    }

    @Override
    public void on(VCEvent event) {
        lastReq = event;

        if (eventPasser == null)
            throw new RuntimeException("Cannot send VCEvent: no passer available.");
        eventPasser.on(event);
    }

    private void on(ErrAction event) {
        cli.repeatState("");
    }

    private void on(ErrActiveLeaderDiscarded event) {
        cli.repeatState("");
    }

    private void on(ErrBuyDevCard event) {
        cli.repeatState("");
    }

    private void on(ErrCardRequirements event) {
        cli.repeatState("");
    }

    private void on(ErrInitialChoice event) {
        cli.repeatState("");
    }

    private void on(ErrNewGame event) {
        cli.repeatState(event.isInvalidPlayersCount() ? "Invalid players count." : "You are not supposed to choose");
    }

    private void on(ErrNickname event) {
        cli.repeatState("Nickname is invalid. Reason: " + event.toString());
    }

    private void on(ErrObjectNotOwned event) {
        cli.repeatState("You are not the owner. Try again.");
    }

    private void on(ErrReplacedTransRecipe event) {
        cli.repeatState("");
    }

    private void on(ErrResourceReplacement event) {
        cli.repeatState("");
    }

    private void on(ErrResourceTransfer event) {
        cli.repeatState("");
    }

    private void on(ErrProtocol event) {
        cli.repeatState("Error. Elaborated input was not sent in a proper JSON format");
    }

    private void on(ErrRuntime event) {
        cli.repeatState("Helo your computer has virus");
    }

    private void on(ReqHeartbeat event) {
        // handled in the ClientServerHandler
        // this.on(new ResHeartbeat());
    }

    private void on(ResGoodbye event) {
        cli.quit();
    }

    private void on(ResWelcome event) {
        cli.setState(new InputNicknameState());
    }

    private void on(UpdateActionToken event) {

    }

    private void on(UpdateBookedSeats event) {
        if(event.canPrepareNewGame().equals(cli.getNickname()))
            cli.setState(new InputPlayersCountState());
        else cli.setState(new WaitingState(event.getBookedSeats()));
    }

    private void on(UpdateCurrentPlayer event) {
        cache.setCurrentPlayer(event.getPlayer());
    }

    private void on(UpdateDevCardGrid event) {
        cache.setDevCardGrid(event.getCards());
    }

    private void on(UpdateDevCardSlot event) {

    }

    private void on(UpdateFaithPoints event) {
        cache.setFaithPoints(event.getFaithPoints());
    }

    private void on(UpdateGameEnd event) {

    }

    private void on(UpdateGameResume event) {

    }

    private void on(UpdateGameStart event) {
        cache.setVictoryPoints(0);
        cache.setActionTokens(event.getActionTokens());
        cache.setContainers(event.getResContainers());
        cache.setFaithPoints(0);
        //... might as well use a constructor instead of all these setters
    }

    private void on(UpdateJoinGame event) {
//        cli.setState(new WaitingState(event.getPlayersCount()));
    }

    private void on(UpdateLastRound event) {
        cache.setLastRound();
    }

    private void on(UpdateLeader event) {

    }

    private void on(UpdateLeadersHand event) {
        cli.setState(new SetupLeadersState(4 - event.getLeaders().size()));
    }

    private void on(UpdateLeadersHandCount event) {
        // here the confirmation of the above
    }

    private void on(UpdateMarket event) {
        cache.setMarket(event.getMarket());
    }

    private void on(UpdatePlayer event) {

    }

    private void on(UpdatePlayerStatus event) {

    }

    private void on(UpdateResourceContainer event) {
        cache.setContainer(event.getResContainer());
    }

    private void on(UpdateSetupDone event) {
        cli.setState(new TurnBeforeActionState());
    }

    private void on(UpdateVaticanSection event) {

    }

    private void on(UpdateVictoryPoints event) {
        cache.setVictoryPoints(event.getVictoryPoints());
    }
}
