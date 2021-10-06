package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;

import java.util.concurrent.*;

/**
 * Non-blocking event dispatcher.
 */
public class AsynchronousEventDispatcher extends EventDispatcher implements AutoCloseable {
    /** The executor doing the dispatching. */
    private final ExecutorService executor;

    /** Class constructor. */
    public AsynchronousEventDispatcher() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public <T extends Event> void dispatch(T event) {
        if (executor.isShutdown())
            return;
        asyncDispatch(event);
    }

    /**
     * Dispatches an event synchronously, waiting for its delivery.
     *
     * @param <T> the type of the event to dispatch
     * @param event the event to dispatch
     */
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

    /**
     * Dispatches an event asynchronously, not waiting for its delivery.
     *
     * @param <T> the type of the event to dispatch
     * @param event the event to dispatch
     * @return a Future representing pending completion of the dispatching process
     */
    private <T extends Event> Future<?> asyncDispatch(T event) {
        return executor.submit(() -> super.dispatch(event));
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
