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
        listening = true;
        while (listening) {
            try {
                dispatch(eventQueue.take());
            } catch (InterruptedException e) {
                listening = false;
            }
        }
    }

    @Override
    public void send(Event event) {
        eventQueue.add(event);
    }
}
