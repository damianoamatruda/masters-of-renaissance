package it.polimi.ingsw.client;

import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.io.InputStream;

public class OfflineClient implements Network {
    private static final String defaultGameConfigPath = "/config/config.json";

    private final View view;
    private final InputStream gameConfigStream;
    private Lobby model;
    private Controller controller;

    public OfflineClient(View view, InputStream gameConfigStream) {
        this.view = view;
        this.gameConfigStream = gameConfigStream != null ? gameConfigStream : getClass().getResourceAsStream(defaultGameConfigPath);
        this.model = null;
        this.controller = null;
    }

    public OfflineClient(View view) {
        this(view, null);
    }

    public void start() {
        GameFactory gameFactory = new FileGameFactory(gameConfigStream);
        model = new Lobby(gameFactory);
        controller = new Controller(model);

        view.registerOnModelLobby(model);
        controller.registerOnVC(view);
    }

    public void stop() {
        if (model != null) {
            view.unregisterOnModelLobby(model);
            model = null;
        }

        if (controller != null) {
            controller.unregisterOnVC(view);
            controller = null;
        }
    }
}
