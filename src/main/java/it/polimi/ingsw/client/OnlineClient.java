package it.polimi.ingsw.client;

import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineClient implements Network {
    private final View view;
    private final String host;
    private final int port;
    private final ExecutorService executor;
    private final NetworkProtocol protocol;
    private Socket socket;
    private NetworkHandler networkHandler;
    private EventListener<VCEvent> vcEventListener;

    public OnlineClient(View view, String host, int port) {
        this.view = view;
        this.host = host;
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.protocol = new NetworkProtocol();
        this.socket = null;
        this.networkHandler = null;
        this.vcEventListener = null;
    }

    public void start() throws IOException {
        socket = new Socket(host, port);

        networkHandler = new ClientServerHandler(socket, protocol);

        view.registerOnModelLobby(networkHandler);
        view.registerOnModelGameContext(networkHandler);
        view.registerOnModelGame(networkHandler);
        view.registerOnModelPlayer(networkHandler);
        view.addEventListener(VCEvent.class, vcEventListener = networkHandler::send);

        networkHandler.setOnStop(() -> {
            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGameContext(networkHandler);
            view.unregisterOnModelGame(networkHandler);
            view.unregisterOnModelPlayer(networkHandler);
            stop();
        });

        executor.submit(networkHandler);
    }

    public void stop() {
        executor.shutdownNow();

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            socket = null;
        }

        if (networkHandler != null) {
            networkHandler.stop();
            networkHandler = null;
        }

        if (vcEventListener != null) {
            view.removeEventListener(VCEvent.class, vcEventListener);
            vcEventListener = null;
        }
    }
}
