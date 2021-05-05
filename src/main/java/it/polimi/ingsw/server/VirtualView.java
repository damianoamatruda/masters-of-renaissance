package it.polimi.ingsw.server;

import it.polimi.ingsw.common.ControllerObservable;
import it.polimi.ingsw.common.ModelObserver;
import it.polimi.ingsw.common.events.*;

/**
 * This class represents a virtual view.
 */
public class VirtualView extends ControllerObservable implements ModelObserver {
    /** The event sender of the view. */
    private MVEventSender eventSender;

    /**
     * Class constructor.
     *
     * @param controller the controller observing the view
     */
    public VirtualView(Controller controller) {
        super(controller);
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
}
