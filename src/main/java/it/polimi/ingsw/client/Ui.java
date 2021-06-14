package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;

public class Ui {
    private final View view;
    private final ViewModel viewModel;
    
    private Network client;
    private InputStream gameConfigStream;
    private boolean offline;

    public Ui() {
        view = new View();
        viewModel = new ViewModel();
        offline = false;
        gameConfigStream = null;
    }

    public View getView() {
        return view;
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void dispatch(Event event) {
        view.dispatch(event);
    }
    
    public Optional<InputStream> getGameConfigStream() {
        return Optional.ofNullable(gameConfigStream);
    }

    public void setGameConfigStream(InputStream gameConfigStream) {
        this.gameConfigStream = gameConfigStream;
    }

    public boolean isOffline() {
        return offline;
    }

    public void openOfflineClient() {
        closeClient();
        client = new OfflineClient(view, gameConfigStream);
        client.open();
        offline = true;
    }

    public void openOnlineClient(String host, int port) throws IOException {
        closeClient();
        client = new OnlineClient(view, host, port);
        client.open();
        offline = false;
    }

    public void closeClient() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public void stop() {
        closeClient();
        view.close();
    }
}
