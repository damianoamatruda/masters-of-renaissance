package it.polimi.ingsw.client;

import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OfflineClient implements Network {
    private static final String gameConfigPath = "/config/config.json";

    private final View view;
    private final ExecutorService executor;
    private Controller controller;
    private View virtualView;

    public OfflineClient(View view) {
        this.view = view;
        this.executor = Executors.newCachedThreadPool();
        this.controller = null;
        this.virtualView = null;
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
        Lobby model = new Lobby(gameFactory);
        controller = new Controller(model);

        NetworkHandler networkHandler = new ClientLocalHandler();

        networkHandler.registerOnVC(view);

        virtualView = new View();
        virtualView.registerOnModelLobby(model);
        virtualView.registerOnVC(networkHandler);
        controller.registerOnVC(virtualView);

        view.registerOnModelLobby(virtualView);
        view.registerOnModelGameContext(virtualView);
        view.registerOnModelGame(virtualView);
        view.registerOnModelPlayer(virtualView);

        executor.submit(networkHandler);
    }

    public void stop() {
        executor.shutdownNow();

        if (controller != null)
            controller.unregisterOnVC(virtualView);

        if (virtualView != null) {
            view.unregisterOnModelLobby(virtualView);
            view.unregisterOnModelGameContext(virtualView);
            view.unregisterOnModelGame(virtualView);
            view.unregisterOnModelPlayer(virtualView);
            virtualView = null;
        }
    }
}
