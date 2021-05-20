package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;
import it.polimi.ingsw.common.reducedmodel.*;

public class CliView extends View implements EventListener<VCEvent> {
    private final ReducedGame cache;
    private final Cli cli;
    private VCEvent lastReq;
    private final ReducedObjectPrinter printer;

    /**
     * Class constructor.
     */
    public CliView(ReducedGame cache, Cli cli, ReducedObjectPrinter printer) {
        this.cache = cache;
        this.cli = cli;
        this.printer = printer;

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
        cli.repeatState(event.getReason().toString());
    }

    private void on(ErrActiveLeaderDiscarded event) {
        int id = -1;
        try {
            id = ((ReqLeaderAction)lastReq).getLeader();
        } catch (Exception ignored) {}

        cli.repeatState(String.format("Active leader %d tried to be discarded.", id));
    }

    private void on(ErrBuyDevCard event) {
        cli.repeatState(event.isStackEmpty() ?
            "Cannot buy development card. Deck is empty." :
            "Cannot place devcard in slot, level mismatch."); // TODO improve msg
    }

    private void on(ErrCardRequirements event) {
        cli.repeatState(event.getReason());
    }

    private void on(ErrInitialChoice event) {
        cli.repeatState(event.isLeadersChoice() ?
            event.getMissingLeadersCount() == 0 ?
                "Leaders already chosen" :
                String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
            "Resources already chosen");
    }

    private void on(ErrNewGame event) {
        cli.repeatState(event.isInvalidPlayersCount() ?
            "Invalid players count." :
            "You are not supposed to choose the players count for the game.");
    }

    private void on(ErrNickname event) {
        cli.repeatState("Nickname is invalid. Reason: " + event.getReason().toString());
    }

    private void on(ErrObjectNotOwned event) {
        cli.repeatState(String.format("%s %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    private void on(ErrReplacedTransRecipe event) {
        cli.repeatState(
            String.format(
                "Discrepancy in the transaction recipe.\nResource: %s, replaced count: %d, specified shelfmap count: %d",
                event.getResType(), event.getReplacedCount(), event.getShelvesChoiceResCount()));
    }

    private void on(ErrResourceReplacement event) {
        if (event.isExcluded() || event.isNonStorable())
            cli.repeatState(String.format("Error validating transaction request %s: %s resource found.",
                event.isInput() ? "input" : "output",
                event.isNonStorable() ? "nonstorable" : "excluded"));
        else
            cli.repeatState(String.format("Error validating transaction request: %d replaced resources when %d replaceable.",
                event.getReplacedCount(),
                event.getBlanks()));
    }

    private void on(ErrResourceTransfer event) {
        cli.repeatState(String.format("Error transferring resources: resource %s, %s, %s.",
            event.getResType(),
            event.isAdded() ? "added" : "removed",
            event.getReason().toString()));
    }

    private void on(ErrProtocol event) {
        cli.repeatState("Error. Elaborated input was not sent in a proper JSON format");
    }

    private void on(ErrRuntime event) {
        cli.repeatState("Uncaught exception has been thrown. Please retry.");
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
//        cache.setActionTokens();  // how does the update work?
//        might probably put a setState here, not sure yet
    }

    private void on(UpdateBookedSeats event) {
        if(event.canPrepareNewGame().equals(cache.getNickname()))
            cli.setState(new InputPlayersCountState());
        else cli.setState(new WaitingBeforeGameState(event.getBookedSeats()));
    }

    private void on(UpdateCurrentPlayer event) {
        cache.setCurrentPlayer(event.getPlayer());
        if(!event.getPlayer().equals(cache.getNickname()))
            cli.setState(new WaitingForTurnState());
        else if(!(lastReq instanceof ReqNewGame))
            cli.setState(new TurnBeforeActionState());  // make sure the update never arrives during the own turn
    }

    private void on(UpdateDevCardGrid event) {
        cache.setDevCardGrid(event.getCards());
    }

    private void on(UpdateDevCardSlot event) {
        cli.setState(new TurnAfterActionState());
    }

    private void on(UpdateFaithPoints event) {
        cache.setFaithPoints(event.getFaithPoints());
    }

    private void on(UpdateGameEnd event) {

    }

    private void on(UpdateGameResume event) {

    }

    private void on(UpdateGameStart event) {
        cache.setActionTokens(event.getActionTokens());
        cache.setContainers(event.getResContainers());
        cache.setDevelopmentCards(event.getDevelopmentCards());
        cache.setFaithPoints(0);
        cache.setLeaderCards(event.getLeaderCards());
        cache.setPlayers(event.getPlayers());
        cache.setProductions(event.getProductions());
        event.getPlayers().forEach(p -> cache.setVictoryPoints(p, 0));
    }

    private void on(UpdateJoinGame event) {
       cli.setState(new WaitingBeforeGameState(event.getPlayersCount()));
    }

    private void on(UpdateLastRound event) {
        cache.setLastRound();
    }

    private void on(UpdateLeader event) {
        if (event.isActive())
            cache.setPlayerLeaders(cache.getCurrentPlayer(), event.getLeader());
    }

    private void on(UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategamestart
            currplayer
            market
            devcardgrid
            player
            leadershand -> with GS and player has enough info for leader choice */

        event.getLeaders().forEach(id -> cache.setPlayerLeaders(event.getPlayer(), id));

        if(event.getLeaders().size() > cache.getLeadersToChoose())
            cli.setState(new SetupLeadersState(event.getLeaders().size() - cache.getLeadersToChoose()));
        else if(cache.getResourcesToChoose() > 0)
            cli.setState(new SetupResourcesState(cache.getResourcesToChoose()));
    }

    private void on(UpdateLeadersHandCount event) {
        cache.setPlayerLeadersCount(event.getPlayer(), event.getLeadersCount());
    }

    private void on(UpdateMarket event) {
        cache.setMarket(event.getMarket());

        /* if market update originates from my command and not from others' (base action) */
        //might be problematic if this command is sent while not current player, but immediately after sb else activates market
        if(lastReq instanceof ReqTakeFromMarket)
            cli.setState(new TurnAfterActionState());
    }

    private void on(UpdatePlayer event) {
        cache.setResourcesToChoose(event.getPlayerSetup().getInitialResources());
        cache.setLeadersToChoose(event.getPlayerSetup().getChosenLeadersCount());
    }

    private void on(UpdatePlayerStatus event) {
        cache.setPlayerState(event.getPlayer(), event.isActive());
    }

    private void on(UpdateResourceContainer event) {
        cache.setContainer(event.getResContainer());

        /* if update comes from my production activation (base action) */
        if(lastReq instanceof ReqActivateProduction)
            cli.setState(new TurnAfterActionState());
    }

    private void on(UpdateSetupDone event) {
        cli.setState(new TurnBeforeActionState());
    }

    private void on(UpdateVaticanSection event) {
//        cache.setActiveVaticanSection(event.getVaticanSection());
    }

    private void on(UpdateVictoryPoints event) {
        cache.setVictoryPoints(event.getPlayer(), event.getVictoryPoints());
    }
}
