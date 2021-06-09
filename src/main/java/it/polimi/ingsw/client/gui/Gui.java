package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * JavaFX App
 */
public class Gui extends Application {
    private static final String initialSceneFxml = "/assets/gui/mainmenu.fxml";
    private static final double minWidth = 1280;
    private static final double minHeight = 780;
    private static final String title = "Masters of Renaissance";

    private static Gui instance = null;

    private final EventDispatcher eventDispatcher;
    private final View view;
    private Network network;

    private Scene scene;
    private Stage stage;
    private GuiController controller;

    private final ViewModel viewModel;

    private InputStream gameConfigStream;
    private boolean offline;
    private MediaPlayer musicPlayer;
    private double soundFxVolume;

    private final EventListener<ResQuit> resQuitEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateBookedSeats> updateBookedSeatsEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateJoinGame> updateJoinGameEventListener = event -> controller.on(this, event);
    private final EventListener<ErrNewGame> errNewGameEventListener = event -> controller.on(this, event);
    private final EventListener<ErrNickname> errNicknameEventListener = event -> controller.on(this, event);
    private final EventListener<ErrAction> errActionEventListener = event -> controller.on(this, event);
    private final EventListener<ErrActiveLeaderDiscarded> errActiveLeaderDiscardedEventListener = event -> controller.on(this, event);
    private final EventListener<ErrBuyDevCard> errBuyDevCardEventListener = event -> controller.on(this, event);
    private final EventListener<ErrCardRequirements> errCardRequirementsEventListener = event -> controller.on(this, event);
    private final EventListener<ErrInitialChoice> initialChoiceEventListener = event -> controller.on(this, event);
    private final EventListener<ErrNoSuchEntity> errNoSuchEntityEventListener = event -> controller.on(this, event);
    private final EventListener<ErrObjectNotOwned> errObjectNotOwnedEventListener = event -> controller.on(this, event);
    private final EventListener<ErrReplacedTransRecipe> errReplacedTransRecipeEventListener = event -> controller.on(this, event);
    private final EventListener<ErrResourceReplacement> errResourceReplacementEventListener = event -> controller.on(this, event);
    private final EventListener<ErrResourceTransfer> errResourceTransferEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateAction> updateActionEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateActionToken> updateActionTokenEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateCurrentPlayer> updateCurrentPlayerEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateDevCardGrid> updateDevCardGridEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateDevCardSlot> updateDevCardSlotEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateFaithPoints> updateFaithPointsEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateGame> updateGameEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateGameEnd> updateGameEndEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateLastRound> updateLastRoundEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateActivateLeader> updateActivateLeaderEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateLeadersHandCount> updateLeadersHandCountEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateMarket> updateMarketEventListener = event -> controller.on(this, event);
    private final EventListener<UpdatePlayer> updatePlayerEventListener = event -> controller.on(this, event);
    private final EventListener<UpdatePlayerStatus> updatePlayerStatusEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateResourceContainer> updateResourceContainerEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateSetupDone> updateSetupDoneEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateVaticanSection> updateVaticanSectionEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateVictoryPoints> updateVictoryPointsEventListener = event -> controller.on(this, event);
    private final EventListener<UpdateLeadersHand> updateLeadersHandEventListener = event -> controller.on(this, event);

    public static void main(String[] args) {
        launch(args);
    }

    public static Gui getInstance() {
        return instance;
    }

    private static String javaVersion() {
        return System.getProperty("java.version");
    }

    private static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    public Gui() {
        super();
        Gui.instance = this;

        this.eventDispatcher = new EventDispatcher();

        this.view = new View();
        this.view.registerOnVC(this.eventDispatcher);
        this.registerOnMV(this.view);

        this.network = null;

        this.viewModel = new ViewModel();

        this.offline = false;
        this.gameConfigStream = null;
        this.musicPlayer = null;
        this.soundFxVolume = 1;
    }

    /**
     * @return the GUI's viewmodel
     */
    public ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = new FXMLLoader(getClass().getResource(initialSceneFxml)).load();
        scene = new Scene(root, minWidth, minHeight, false, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        primaryStage.setWidth(minWidth);
        primaryStage.setHeight(minHeight);
        primaryStage.setTitle(title);
        primaryStage.show();
        this.stage = primaryStage;
    }

    @Override
    public void stop() {
        if (network != null)
            network.stop();
    }

    void setController(GuiController controller) {
        this.controller = controller;
    }

    void dispatch(Event event) {
        eventDispatcher.dispatch(event);
    }

    void startOfflineClient() {
        stopNetwork();
        network = new OfflineClient(view, gameConfigStream);
        try {
            network.start();
        } catch (IOException ignored) {
        }
        offline = true;
    }

    void startOnlineClient(String host, int port) throws IOException {
        stopNetwork();
        network = new OnlineClient(view, host, port);
        network.start();
        offline = false;
    }

    void stopNetwork() {
        if (network != null) {
            network.stop();
            network = null;
        }
    }

    void setRoot(URL fxml, Consumer<?> callback) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            Parent root = fxmlLoader.load();
            if (callback != null)
                callback.accept(fxmlLoader.getController());
            Platform.runLater(() -> scene.setRoot(root));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void setRoot(URL fxml) {
        setRoot(fxml, null);
    }

    public Stage getStage() {
        return stage;
    }

    void quit() {
        Platform.exit();
        System.exit(0);
    }

    public Optional<InputStream> getGameConfigStream() {
        return Optional.ofNullable(gameConfigStream);
    }

    public void setGameConfigStream(InputStream gameConfigStream) {
        this.gameConfigStream = gameConfigStream;
    }

    boolean isOffline() {
        return offline;
    }

    public Optional<MediaPlayer> getMusicPlayer() {
        return Optional.ofNullable(musicPlayer);
    }

    void setMusicPlayer(MediaPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public double getSoundFxVolume() {
        return soundFxVolume;
    }

    public void setSoundFxVolume(double soundFxVolume) {
        this.soundFxVolume = soundFxVolume;
    }

    private void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, resQuitEventListener);
        view.addEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        view.addEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        view.addEventListener(ErrNewGame.class, errNewGameEventListener);
        view.addEventListener(ErrNickname.class, errNicknameEventListener);
        view.addEventListener(ErrAction.class, errActionEventListener);
        view.addEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        view.addEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        view.addEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        view.addEventListener(ErrInitialChoice.class, initialChoiceEventListener);
        view.addEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        view.addEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        view.addEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        view.addEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        view.addEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        view.addEventListener(UpdateAction.class, updateActionEventListener);
        view.addEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        view.addEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        view.addEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        view.addEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        view.addEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        view.addEventListener(UpdateGame.class, updateGameEventListener);
        view.addEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        view.addEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        view.addEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        view.addEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        view.addEventListener(UpdateMarket.class, updateMarketEventListener);
        view.addEventListener(UpdatePlayer.class, updatePlayerEventListener);
        view.addEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        view.addEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        view.addEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        view.addEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        view.addEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
        view.addEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
    }

    private void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, resQuitEventListener);
        view.removeEventListener(UpdateBookedSeats.class, updateBookedSeatsEventListener);
        view.removeEventListener(UpdateJoinGame.class, updateJoinGameEventListener);
        view.removeEventListener(ErrNewGame.class, errNewGameEventListener);
        view.removeEventListener(ErrNickname.class, errNicknameEventListener);
        view.removeEventListener(ErrAction.class, errActionEventListener);
        view.removeEventListener(ErrActiveLeaderDiscarded.class, errActiveLeaderDiscardedEventListener);
        view.removeEventListener(ErrBuyDevCard.class, errBuyDevCardEventListener);
        view.removeEventListener(ErrCardRequirements.class, errCardRequirementsEventListener);
        view.removeEventListener(ErrInitialChoice.class, initialChoiceEventListener);
        view.removeEventListener(ErrNoSuchEntity.class, errNoSuchEntityEventListener);
        view.removeEventListener(ErrObjectNotOwned.class, errObjectNotOwnedEventListener);
        view.removeEventListener(ErrReplacedTransRecipe.class, errReplacedTransRecipeEventListener);
        view.removeEventListener(ErrResourceReplacement.class, errResourceReplacementEventListener);
        view.removeEventListener(ErrResourceTransfer.class, errResourceTransferEventListener);
        view.removeEventListener(UpdateAction.class, updateActionEventListener);
        view.removeEventListener(UpdateActionToken.class, updateActionTokenEventListener);
        view.removeEventListener(UpdateCurrentPlayer.class, updateCurrentPlayerEventListener);
        view.removeEventListener(UpdateDevCardGrid.class, updateDevCardGridEventListener);
        view.removeEventListener(UpdateDevCardSlot.class, updateDevCardSlotEventListener);
        view.removeEventListener(UpdateFaithPoints.class, updateFaithPointsEventListener);
        view.removeEventListener(UpdateGame.class, updateGameEventListener);
        view.removeEventListener(UpdateGameEnd.class, updateGameEndEventListener);
        view.removeEventListener(UpdateLastRound.class, updateLastRoundEventListener);
        view.removeEventListener(UpdateActivateLeader.class, updateActivateLeaderEventListener);
        view.removeEventListener(UpdateLeadersHandCount.class, updateLeadersHandCountEventListener);
        view.removeEventListener(UpdateMarket.class, updateMarketEventListener);
        view.removeEventListener(UpdatePlayer.class, updatePlayerEventListener);
        view.removeEventListener(UpdatePlayerStatus.class, updatePlayerStatusEventListener);
        view.removeEventListener(UpdateResourceContainer.class, updateResourceContainerEventListener);
        view.removeEventListener(UpdateSetupDone.class, updateSetupDoneEventListener);
        view.removeEventListener(UpdateVaticanSection.class, updateVaticanSectionEventListener);
        view.removeEventListener(UpdateVictoryPoints.class, updateVictoryPointsEventListener);
        view.removeEventListener(UpdateLeadersHand.class, updateLeadersHandEventListener);
    }
}
