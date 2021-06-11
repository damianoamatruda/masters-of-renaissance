package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousEventDispatcher extends EventDispatcher implements AutoCloseable {
    private final ExecutorService executor;

    public AsynchronousEventDispatcher() {
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public <T extends Event> void dispatch(T event) {
        executor.execute(() -> super.dispatch(event));
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
