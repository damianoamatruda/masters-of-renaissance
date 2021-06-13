package it.polimi.ingsw.client;

import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineClient implements Network {
    private final View view;
    private final ExecutorService executor;
    private final NetworkHandler networkHandler;
    private EventListener<VCEvent> vcEventListener;

    public OnlineClient(View view, String host, int port) throws IOException {
        Socket socket = new Socket(host, port);

        this.view = view;
        this.executor = Executors.newCachedThreadPool();

        this.networkHandler = new ClientServerHandler(socket, new NetworkProtocol());
        this.networkHandler.setOnClose(() -> {
            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGameContext(networkHandler);

            close();
        });

        this.vcEventListener = null;
    }

    @Override
    public void open() {
        view.registerOnModelLobby(networkHandler);
        view.registerOnModelGameContext(networkHandler);
        view.addEventListener(VCEvent.class, vcEventListener = networkHandler::send);

        executor.submit(networkHandler);
    }

    @Override
    public void close() {
        view.removeEventListener(VCEvent.class, vcEventListener);
        executor.shutdownNow();
        networkHandler.close();
    }
}
