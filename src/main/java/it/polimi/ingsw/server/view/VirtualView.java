package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.MVEventSender;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.events.*;

/**
 * This class represents a virtual view.
 */
public class VirtualView implements View {
    /** The controller observing the view. */
    private final Controller controller;

    /** The event sender of the view. */
    private MVEventSender eventSender;

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
    public void setMessageSender(MVEventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void notify(ReqQuit event) {
        controller.update(this, event);
    }

    @Override
    public void notify(ReqNickname event) {
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
    public void notify(ReqPlayersCount event) {
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
    public void notify(ReqTurnEnd event) {
        controller.update(this, event);
    }

    @Override
    public void update(ResWelcome event) {
        if (eventSender != null)
            eventSender.send(event);
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
    public void update(ErrCommunication event) {
        if (eventSender != null)
            eventSender.send(event);
    }

    @Override
    public void update(ErrController event) {
        if (eventSender != null)
            eventSender.send(event);
    }

    @Override
    public void update(ResNickname event) {
        if (eventSender != null)
            eventSender.send(event);
    }

    @Override
    public void update(ErrNickname event) {
        if (eventSender != null)
            eventSender.send(event);
    }

    @Override
    public void update(ResGameStarted event) {
        if (eventSender != null)
            eventSender.send(event);
    }

    @Override
    public void update(ErrAction event) {
        if (eventSender != null)
            eventSender.send(event);
    }
}
