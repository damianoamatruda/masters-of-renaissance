package it.polimi.ingsw.client;

import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OfflineClient implements Network {
    private static final String defaultGameConfigPath = "/config/config.json";

    private final View view;
    private final InputStream gameConfigStream;
    private final ExecutorService executor;
    private Controller controller;
    private NetworkHandler networkHandler;

    public OfflineClient(View view, InputStream gameConfigStream) {
        this.view = view;
        this.gameConfigStream = gameConfigStream != null ? gameConfigStream : getClass().getResourceAsStream(defaultGameConfigPath);
        this.executor = Executors.newCachedThreadPool();
        this.controller = null;
        this.networkHandler = null;
    }

    public OfflineClient(View view) {
        this(view, null);
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(gameConfigStream);
        Lobby model = new Lobby(gameFactory);
        controller = new Controller(model);

        networkHandler = new ClientLocalHandler();

        networkHandler.registerOnModelLobby(model);

        view.registerOnModelLobby(networkHandler);
        view.registerOnModelGameContext(networkHandler);
        view.registerOnModelGame(networkHandler);
        view.registerOnModelPlayer(networkHandler);
        controller.registerOnVC(view);

        executor.submit(networkHandler);
    }

    public void stop() {
        executor.shutdownNow();

        if (controller != null) {
            controller.unregisterOnVC(view);
            controller = null;
        }

        if (networkHandler != null) {
            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGameContext(networkHandler);
            view.unregisterOnModelGame(networkHandler);
            view.unregisterOnModelPlayer(networkHandler);
            networkHandler.stop();
            networkHandler = null;
        }
    }
}
