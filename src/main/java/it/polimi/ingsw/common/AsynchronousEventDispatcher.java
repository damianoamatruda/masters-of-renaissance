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
        if (executor.isShutdown())
            return;
        executor.submit(() -> super.dispatch(event));
    }

    public <T extends Event> void awaitDispatch(T event) {
        super.dispatch(event);
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    public void closeNow() {
        executor.shutdownNow();
    }
}
