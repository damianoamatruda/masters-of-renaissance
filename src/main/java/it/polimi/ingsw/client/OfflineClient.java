package it.polimi.ingsw.client;

import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.io.InputStream;

/**
 * This class represents the network client used to Play Offline.
 */
public class OfflineClient implements Network {
    private static final String defaultGameConfigPath = "/config/config.json";
    private final View view;
    private final Lobby model;
    private final Controller controller;

    public OfflineClient(View view, InputStream gameConfigStream) {
        this.view = view;
        this.model = new Lobby(new FileGameFactory(gameConfigStream != null ? gameConfigStream : getClass().getResourceAsStream(defaultGameConfigPath)));
        this.controller = new Controller(model);
    }

    public OfflineClient(View view) {
        this(view, null);
    }

    @Override
    public void open() {
        view.registerOnModelLobby(model);
        controller.registerOnVC(view);
    }

    @Override
    public void close() {
        view.unregisterOnModelLobby(model);
        controller.unregisterOnVC(view);
        model.close();
    }
}
