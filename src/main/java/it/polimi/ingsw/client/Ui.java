package it.polimi.ingsw.client;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Ui extends View {
    private ViewModel viewModel;
    private Network client;
    private UiController controller;
    private InputStream gameConfigStream;
    private byte[] config;
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[16777216];

        int byteCount;
        try {
            while ((byteCount = gameConfigStream.read(buffer)) != -1)
                baos.write(buffer, 0, byteCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.config = baos.toByteArray();
    }

    public boolean isOffline() {
        return offline;
    }

    public void openOfflineClient() {
        closeClient();
        client = new OfflineClient(this, config == null ? null : new ByteArrayInputStream(config));
        client.open();
        offline = true;
        viewModel = new ViewModel();
    }

    public void openOnlineClient(String host, int port) throws IOException {
        closeClient();
        client = new OnlineClient(this, host, port);
        client.open();
        offline = false;
        viewModel = new ViewModel();
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
        setUpdateDevSlotEventListener(event -> controller.on(event));
        setUpdateFaithPointsEventListener(event -> controller.on(event));
        setUpdateGameEventListener(event -> controller.on(event));
        setUpdateGameEndEventListener(event -> controller.on(event));
        setUpdateLastRoundEventListener(event -> controller.on(event));
        setUpdateActivateLeaderEventListener(event -> controller.on(event));
        setUpdateLeadersHandCountEventListener(event -> controller.on(event));
        setUpdateMarketEventListener(event -> controller.on(event));
        setUpdatePlayerStatusEventListener(event -> controller.on(event));
        setUpdateResourceContainerEventListener(event -> controller.on(event));
        setUpdateSetupDoneEventListener(event -> controller.on(event));
        setUpdateVaticanSectionEventListener(event -> controller.on(event));
        setUpdateVictoryPointsEventListener(event -> controller.on(event));
        setUpdateLeadersHandEventListener(event -> controller.on(event));
    }
}
