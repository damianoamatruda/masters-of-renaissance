package it.polimi.ingsw.client;

import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrServerUnavailable;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.IOException;
import java.net.Socket;

/**
 * This class represents the network client used to Play Online.
 */
public class OnlineClient implements Network {
    private static final int timeout = 30000;
    private final View view;
    private final NetworkHandler networkHandler;
    private final Thread networkHandlerThread;
    private final EventListener<VCEvent> vcEventListener;

    public OnlineClient(View view, String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        this.view = view;
        this.networkHandler = new NetworkHandler(socket, new NetworkProtocol(), (input, protocol) -> protocol.processInputAsMVEvent(input), timeout);
        this.networkHandlerThread = new Thread(networkHandler);
        this.vcEventListener = networkHandler::send;
    }

    @Override
    public void open() {
        view.registerOnModelLobby(networkHandler);
        view.registerOnModelGameContext(networkHandler);
        view.addEventListener(VCEvent.class, vcEventListener);

        networkHandler.setOnClose(() -> {
            networkHandler.awaitDispatch(new ErrServerUnavailable());

            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGameContext(networkHandler);
            view.removeEventListener(VCEvent.class, vcEventListener);
        });

        networkHandlerThread.start();
    }

    @Override
    public void close() {
        networkHandler.close();
        networkHandlerThread.interrupt();
    }
}
