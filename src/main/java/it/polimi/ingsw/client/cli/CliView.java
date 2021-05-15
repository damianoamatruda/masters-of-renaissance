package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.VCEventSender;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.*;

public class CliView implements View {
    /** The event sender of the view. */
    private VCEventSender eventSender;

    /**
     * Class constructor.
     *
     * @param controller the controller observing the view
     */
    public CliView(Controller controller) {
        this.eventSender = null;
    }

    /**
     * Sets the event sender.
     *
     * @param eventSender the event sender
     */
    public void setEventSender(VCEventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void update(MVEvent event) {

    }

    @Override
    public void update(ResGoodbye event) {

    }

    @Override
    public void update(ResWelcome event) {

    }

    @Override
    public void update(ErrAction event) {

    }

    @Override
    public void update(ErrProtocol event) {

    }

    @Override
    public void update(ErrRuntime event) {

    }

    @Override
    public void update(UpdateActionToken event) {

    }

    @Override
    public void update(UpdateBookedSeats event) {

    }

    @Override
    public void update(UpdateCurrentPlayer event) {

    }

    @Override
    public void update(UpdateDevCardGrid event) {

    }

    @Override
    public void update(UpdateDevCardSlot event) {

    }

    @Override
    public void update(UpdateFaithPoints event) {

    }

    @Override
    public void update(UpdateGameEnd event) {

    }

    @Override
    public void update(UpdateGameResume event) {

    }

    @Override
    public void update(UpdateGameStart event) {

    }

    @Override
    public void update(UpdateJoinGame event) {

    }

    @Override
    public void update(UpdateLastRound event) {

    }

    @Override
    public void update(UpdateLeader event) {

    }

    @Override
    public void update(UpdateLeadersHand event) {

    }

    @Override
    public void update(UpdateMarket event) {

    }

    @Override
    public void update(UpdatePlayerStatus event) {

    }

    @Override
    public void update(UpdateResourceContainer event) {

    }

    @Override
    public void update(UpdateSetupDone event) {

    }

    @Override
    public void update(UpdateVaticanSection event) {

    }

    @Override
    public void update(UpdateVictoryPoints event) {

    }

    @Override
    public void notify(ReqGoodbye event) {
        send(event);
    }

    @Override
    public void notify(ReqJoin event) {
        send(event);
    }

    @Override
    public void notify(ReqActivateLeader event) {
        send(event);
    }

    @Override
    public void notify(ReqActivateProduction event) {
        send(event);
    }

    @Override
    public void notify(ReqBuyDevCard event) {
        send(event);
    }

    @Override
    public void notify(ReqDiscardLeader event) {
        send(event);
    }

    @Override
    public void notify(ReqChooseLeaders event) {
        send(event);
    }

    @Override
    public void notify(ReqNewGame event) {
        send(event);
    }

    @Override
    public void notify(ReqChooseResources event) {
        send(event);
    }

    @Override
    public void notify(ReqSwapShelves event) {
        send(event);
    }

    @Override
    public void notify(ReqTakeFromMarket event) {
        send(event);
    }

    @Override
    public void notify(ReqEndTurn event) {
        send(event);
    }

    private void send(VCEvent event) {
        if (eventSender != null)
            eventSender.send(event);
        else
            throw new RuntimeException("Cannot send VCEvent: no sender available");
    }
}
