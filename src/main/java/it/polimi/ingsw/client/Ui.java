package it.polimi.ingsw.client;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Ui extends View {
    private final ViewModel viewModel;
    private Network client;
    private UiController controller;
    private InputStream gameConfigStream;
    private boolean offline;

    public Ui() {
        setListeners();
        viewModel = new ViewModel();
        offline = false;
        gameConfigStream = null;
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public void setController(UiController controller) {
        this.controller = controller;
    }

    public UiController getController() {
        return controller;
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
        client = new OfflineClient(this, gameConfigStream);
        client.open();
        offline = true;
    }

    public void openOnlineClient(String host, int port) throws IOException {
        closeClient();
        client = new OnlineClient(this, host, port);
        client.open();
        offline = false;
    }

    public void closeClient() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public boolean hasClientOpen() {
        return client != null;
    }

    public void stop() {
        closeClient();
        close();
    }

    private void setListeners() {
        setErrNewGameEventListener(event -> controller.on(event));
        setErrNicknameEventListener(event -> controller.on(event));
        setResQuitEventListener(event -> controller.on(event));
        setUpdateBookedSeatsEventListener(event -> controller.on(event));
        setUpdateJoinGameEventListener(event -> controller.on(event));
        setUpdateServerUnavailableEventListener(event -> controller.on(event));
        setErrActionEventListener(event -> controller.on(event));
        setErrActiveLeaderDiscardedEventListener(event -> controller.on(event));
        setErrBuyDevCardEventListener(event -> controller.on(event));
        setErrCardRequirementsEventListener(event -> controller.on(event));
        setErrInitialChoiceEventListener(event -> controller.on(event));
        setErrNoSuchEntityEventListener(event -> controller.on(event));
        setErrObjectNotOwnedEventListener(event -> controller.on(event));
        setErrReplacedTransRecipeEventListener(event -> controller.on(event));
        setErrResourceReplacementEventListener(event -> controller.on(event));
        setErrResourceTransferEventListener(event -> controller.on(event));
        setUpdateActionEventListener(event -> controller.on(event));
        setUpdateActionTokenEventListener(event -> controller.on(event));
        setUpdateCurrentPlayerEventListener(event -> controller.on(event));
        setUpdateDevCardGridEventListener(event -> controller.on(event));
        setUpdateDevCardSlotEventListener(event -> controller.on(event));
        setUpdateFaithPointsEventListener(event -> controller.on(event));
        setUpdateGameEventListener(event -> controller.on(event));
        setUpdateGameEndEventListener(event -> controller.on(event));
        setUpdateLastRoundEventListener(event -> controller.on(event));
        setUpdateActivateLeaderEventListener(event -> controller.on(event));
        setUpdateLeadersHandCountEventListener(event -> controller.on(event));
        setUpdateMarketEventListener(event -> controller.on(event));
        setUpdatePlayerEventListener(event -> controller.on(event));
        setUpdatePlayerStatusEventListener(event -> controller.on(event));
        setUpdateResourceContainerEventListener(event -> controller.on(event));
        setUpdateSetupDoneEventListener(event -> controller.on(event));
        setUpdateVaticanSectionEventListener(event -> controller.on(event));
        setUpdateVictoryPointsEventListener(event -> controller.on(event));
        setUpdateLeadersHandEventListener(event -> controller.on(event));
    }
}
