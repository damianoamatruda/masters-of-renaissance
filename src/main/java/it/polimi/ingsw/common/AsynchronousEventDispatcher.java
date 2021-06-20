package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.*;

public class AsynchronousEventDispatcher extends EventDispatcher implements AutoCloseable {
    private final ExecutorService executor;

    public AsynchronousEventDispatcher() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public <T extends Event> void dispatch(T event) {
        if (executor.isShutdown())
            return;
        asyncDispatch(event);
    }

    public <T extends Event> void awaitDispatch(T event) {
        if (executor.isShutdown())
            return;
        try {
            asyncDispatch(event).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (CancellationException ignored) {
        }
    }

    private <T extends Event> Future<?> asyncDispatch(T event) {
        return executor.submit(() -> super.dispatch(event));
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
