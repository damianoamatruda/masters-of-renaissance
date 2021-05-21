package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

public class LocalClient {
    private final static String gameConfigPath = "/config/config.json";

    private final View view;
    private final Cli cli; // TODO: Make it compatible with other UIs
    private Controller controller;

    public LocalClient(View view, Cli cli) {
        this.view = view;
        this.cli = cli;
        this.controller = null;
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
        Lobby model = new Lobby(gameFactory);
        controller = new Controller(model);

        view.registerOnVC(cli);
        cli.registerOnMV(view);

        controller.registerOnVC(view);
    }

    public void stop() {
        view.unregisterOnVC(cli);
        cli.unregisterOnMV(view);

        if (controller != null)
            controller.unregisterOnVC(view);
    }
}
