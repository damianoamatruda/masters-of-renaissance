package it.polimi.ingsw.client;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientLocalHandler extends NetworkHandler {
    private final BlockingQueue<Event> eventQueue;

    public ClientLocalHandler() {
        super(null, null);
        eventQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        Event event;

        listening = true;
        while (listening) {
            try {
                event = eventQueue.take();
                System.out.println(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
