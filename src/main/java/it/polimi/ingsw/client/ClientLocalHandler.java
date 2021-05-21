package it.polimi.ingsw.client;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientLocalHandler extends NetworkHandler {
    private final BlockingQueue<Event> eventQueue;

    public ClientLocalHandler() {
        super(null, null);
        this.eventQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        Event event;

        listening = true;
        while (listening) {
            try {
                event = eventQueue.take();
            } catch (InterruptedException e) {
                listening = false;
                break;
            }
            dispatch(event);
        }
    }

    @Override
    public void send(Event event) {
        eventQueue.add(event);
    }
}
