package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalClient {
    private final static String gameConfigPath = "/config/config.json";

    private final ExecutorService executor;
    private final View view;
    private final Cli cli; // TODO: Make it compatible with other UIs
    private Controller controller;
    private NetworkHandler networkHandler;

    public LocalClient(View view, Cli cli) {
        this.executor = Executors.newCachedThreadPool();
        this.view = view;
        this.cli = cli;
        this.controller = null;
        this.networkHandler = null;
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
        Lobby model = new Lobby(gameFactory);
        controller = new Controller(model);

        NetworkHandler networkHandler = new ClientLocalHandler();

        view.registerOnVC(cli);
        cli.registerOnMV(view);

        networkHandler.registerOnVC(view);

        View backendView = new View();
        backendView.registerOnVC(networkHandler);
        controller.registerOnVC(backendView);

        view.registerOnModelGame(backendView);
        view.registerOnModelPlayer(backendView);

        executor.submit(networkHandler);
    }

    public void stop() {
        view.unregisterOnVC(cli);
        cli.unregisterOnMV(view);

        executor.shutdown();
        
        if (controller != null)
            controller.unregisterOnVC(view);

        if (networkHandler != null) {
            view.unregisterOnModelGame(networkHandler);
            view.unregisterOnModelPlayer(networkHandler);
            networkHandler = null;
        }
    }
}