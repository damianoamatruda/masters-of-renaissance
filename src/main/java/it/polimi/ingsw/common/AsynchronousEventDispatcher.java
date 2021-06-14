package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    public void close(Runnable callback) {
        if (executor.isShutdown())
            return;
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
        }
        callback.run();
    }
}
