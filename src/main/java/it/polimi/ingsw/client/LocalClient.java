package it.polimi.ingsw.client;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalClient {
    private static final String gameConfigPath = "/config/config.json";

    private final View view;
    private final ExecutorService executor;
    private Controller controller;
    private NetworkHandler networkHandler;

    public LocalClient(View view) {
        this.view = view;
        this.executor = Executors.newCachedThreadPool();
        this.controller = null;
        this.networkHandler = null;
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
        Lobby model = new Lobby(gameFactory);
        controller = new Controller(model);

        NetworkHandler networkHandler = new ClientLocalHandler();

        networkHandler.registerOnVC(view);

        View virtualView = new View();
        virtualView.registerOnModelLobby(model);
        virtualView.registerOnVC(networkHandler);
        controller.registerOnVC(virtualView);

        view.registerOnModelLobby(virtualView);
        view.registerOnModelGame(virtualView);
        view.registerOnModelPlayer(virtualView);

        executor.submit(networkHandler);
    }

    public void stop() {
        executor.shutdown();

        if (controller != null)
            controller.unregisterOnVC(view);

        if (networkHandler != null) {
            view.unregisterOnModelLobby(networkHandler);
            view.unregisterOnModelGame(networkHandler);
            view.unregisterOnModelPlayer(networkHandler);
            networkHandler = null;
        }
    }
}
