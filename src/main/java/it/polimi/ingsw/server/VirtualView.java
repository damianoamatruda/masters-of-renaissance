package it.polimi.ingsw.server;

import it.polimi.ingsw.common.EventSender;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.*;

/**
 * This class represents a virtual view.
 */
public class VirtualView implements View {
    /** The controller observing the view. */
    private final Controller controller;

    /** The event sender of the view. */
    protected EventSender eventSender;

    /**
     * Class constructor.
     *
     * @param controller the controller observing the view
     */
    public VirtualView(Controller controller) {
        this.controller = controller;
        this.eventSender = null;
    }

    /**
     * Sets the event sender.
     *
     * @param eventSender the event sender
     */
    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void update(ResGoodbye event) {
        if (eventSender != null) {
            eventSender.send(event);
            eventSender.stop();
            eventSender = null;
        }
    }

    @Override
    public void update(ResWelcome event) {
        send(event);
    }

    @Override
    public void update(ErrAction event) {
        send(event);
    }

    @Override
    public void update(ErrProtocol event) {
        send(event);
    }

    @Override
    public void update(ErrRuntime event) {
        send(event);
    }

    @Override
    public void update(UpdateActionToken event) {
        send(event);
    }

    @Override
    public void update(UpdateBookedSeats event) {
        send(event);
    }

    @Override
    public void update(UpdateCurrentPlayer event) {
        send(event);
    }

    @Override
    public void update(UpdateDevCardGrid event) {
        send(event);
    }

    @Override
    public void update(UpdateDevCardSlot event) {
        send(event);
    }

    @Override
    public void update(UpdateFaithPoints event) {
        send(event);
    }

    @Override
    public void update(UpdateGameEnd event) {
        send(event);
    }

    @Override
    public void update(UpdateGameResume event) {
        send(event);
    }

    @Override
    public void update(UpdateGameStart event) {
        send(event);
    }

    @Override
    public void update(UpdateJoinGame event) {
        send(event);
    }

    @Override
    public void update(UpdateLastRound event) {
        send(event);
    }

    @Override
    public void update(UpdateLeader event) {
        send(event);
    }

    @Override
    public void update(UpdateLeadersHand event) {
        send(event);
    }

    @Override
    public void update(UpdateMarket event) {
        send(event);
    }

    @Override
    public void update(UpdatePlayerStatus event) {
        send(event);
    }

    @Override
    public void update(UpdateResourceContainer event) {
        send(event);
    }

    @Override
    public void update(UpdateSetupDone event) {
        send(event);
    }

    @Override
    public void update(UpdateVaticanSection event) {
        send(event);
    }

    @Override
    public void update(UpdateVictoryPoints event) {
        send(event);
    }

    @Override
    public void notify(ReqGoodbye event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqJoin event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqActivateLeader event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqActivateProduction event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqBuyDevCard event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqDiscardLeader event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqChooseLeaders event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqNewGame event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqChooseResources event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqSwapShelves event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqTakeFromMarket event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqEndTurn event) {
        controller.update(this, event);
    }

    private void send(MVEvent event) {
        if (eventSender != null)
            eventSender.send(event);
        else
            throw new RuntimeException("Cannot send MVEvent: no sender available.");
    }
}
