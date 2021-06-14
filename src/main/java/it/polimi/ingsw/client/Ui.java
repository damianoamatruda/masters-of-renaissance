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

    private UiController controller;

    private InputStream gameConfigStream;
    private boolean offline;

    public Ui() {
        view = new View();
        viewModel = new ViewModel();

        setViewListeners();

        offline = false;
        gameConfigStream = null;
    }

    public View getView() {
        return view;
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void setController(UiController controller) {
        this.controller = controller;
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

    private void setViewListeners() {
        view.setResQuitEventListener(event -> controller.on(event));
        view.setUpdateBookedSeatsEventListener(event -> controller.on(event));
        view.setUpdateJoinGameEventListener(event -> controller.on(event));
        view.setErrNewGameEventListener(event -> controller.on(event));
        view.setErrNicknameEventListener(event -> controller.on(event));
        view.setErrActionEventListener(event -> controller.on(event));
        view.setErrActiveLeaderDiscardedEventListener(event -> controller.on(event));
        view.setErrBuyDevCardEventListener(event -> controller.on(event));
        view.setErrCardRequirementsEventListener(event -> controller.on(event));
        view.setErrInitialChoiceEventListener(event -> controller.on(event));
        view.setErrNoSuchEntityEventListener(event -> controller.on(event));
        view.setErrObjectNotOwnedEventListener(event -> controller.on(event));
        view.setErrReplacedTransRecipeEventListener(event -> controller.on(event));
        view.setErrResourceReplacementEventListener(event -> controller.on(event));
        view.setErrResourceTransferEventListener(event -> controller.on(event));
        view.setUpdateActionEventListener(event -> controller.on(event));
        view.setUpdateActionTokenEventListener(event -> controller.on(event));
        view.setUpdateCurrentPlayerEventListener(event -> controller.on(event));
        view.setUpdateDevCardGridEventListener(event -> controller.on(event));
        view.setUpdateDevCardSlotEventListener(event -> controller.on(event));
        view.setUpdateFaithPointsEventListener(event -> controller.on(event));
        view.setUpdateGameEventListener(event -> controller.on(event));
        view.setUpdateGameEndEventListener(event -> controller.on(event));
        view.setUpdateLastRoundEventListener(event -> controller.on(event));
        view.setUpdateActivateLeaderEventListener(event -> controller.on(event));
        view.setUpdateLeadersHandCountEventListener(event -> controller.on(event));
        view.setUpdateMarketEventListener(event -> controller.on(event));
        view.setUpdatePlayerEventListener(event -> controller.on(event));
        view.setUpdatePlayerStatusEventListener(event -> controller.on(event));
        view.setUpdateResourceContainerEventListener(event -> controller.on(event));
        view.setUpdateSetupDoneEventListener(event -> controller.on(event));
        view.setUpdateVaticanSectionEventListener(event -> controller.on(event));
        view.setUpdateVictoryPointsEventListener(event -> controller.on(event));
        view.setUpdateLeadersHandEventListener(event -> controller.on(event));
    }
}
