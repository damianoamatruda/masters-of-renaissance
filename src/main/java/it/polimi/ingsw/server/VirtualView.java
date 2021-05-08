package it.polimi.ingsw.server;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.*;

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
    public void setEventSender(MVEventSender eventSender) {
        this.eventSender = eventSender;
    }


    // ModelObserver section

    @Override
    public void update(ResGoodbye event) {
        if (eventSender != null) {
            eventSender.send(event);
            eventSender.stop();
            eventSender = null;
        }
    }

    @Override
    public void update(MVEvent event) {
        if (eventSender != null)
            eventSender.send(event);
        else throw new RuntimeException("Cannot send MVEvent: no sender available");
    }


    // ControllerObservable section

    @Override
    public void notify(ReqQuit event) {
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
    public void notify(ReqTurnEnd event) {
        controller.update(this, event);
    }
}
