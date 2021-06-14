package it.polimi.ingsw.client;

import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.events.mvevents.UpdateServerUnavailable;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class OnlineClient implements Network {
    private static final Logger LOGGER = Logger.getLogger(OnlineClient.class.getName());
    private static final int timeout = 30000;
    private final View view;
    private final ExecutorService executor;
    private final NetworkHandler networkHandler;
    private final EventListener<VCEvent> vcEventListener;

    public OnlineClient(View view, String host, int port) throws IOException {
        Socket socket = new Socket(host, port);

        this.view = view;

        executor = Executors.newCachedThreadPool();
        networkHandler = new NetworkHandler(socket, new NetworkProtocol(), (input, protocol) -> protocol.processInputAsMVEvent(input), timeout);
        vcEventListener = networkHandler::send;
    }

    @Override
    public void open() {
        view.registerOnModelLobby(networkHandler);
        view.registerOnModelGameContext(networkHandler);
        view.addEventListener(VCEvent.class, vcEventListener);

        networkHandler.setOnClose(() -> {
            networkHandler.awaitDispatch(new UpdateServerUnavailable());

            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGameContext(networkHandler);
            view.removeEventListener(VCEvent.class, vcEventListener);
        });

        executor.submit(networkHandler);
    }

    @Override
    public void close() {
        executor.shutdown();
        networkHandler.close();
    }
}
