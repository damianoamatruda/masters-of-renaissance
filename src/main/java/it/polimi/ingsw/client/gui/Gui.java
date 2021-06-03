package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.EventDispatcher;
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

    private static Gui instance = null;

    private final EventDispatcher eventDispatcher;
    private final View view;
    private Network network;

    private Scene scene;
    private GuiController controller;

    private final ViewModel viewModel;

    private boolean offline;
    private MediaPlayer musicPlayer;
    private double soundFxVolume;

    public static void main(String[] args) {
        // Platform.setImplicitExit(false); // makes the runLater invocations not fail
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
        primaryStage.setTitle("Masters of Renaissance");
        primaryStage.show();
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
        network = new OfflineClient(view);
        try {
            network.start();
        } catch (IOException ignored) {
        }
        offline = true;
    }

    void startOnlineClient(String host, int port) throws IOException {
        network = new OnlineClient(view, host, port);
        network.start();
        offline = false;
    }

    void setRoot(URL fxml, Consumer<?> callback) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        Parent root = fxmlLoader.load();
        if (callback != null)
            callback.accept(fxmlLoader.getController());
        scene.setRoot(root);
    }

    void setRoot(URL fxml) throws IOException {
        setRoot(fxml, null);
    }

    Optional<MediaPlayer> getMusicPlayer() {
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

    boolean isOffline() {
        return offline;
    }

    void quit() {
        Platform.exit();
        System.exit(0);
    }

    private void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, event -> controller.on(this, event));
        view.addEventListener(UpdateBookedSeats.class, event -> controller.on(this, event));
        view.addEventListener(UpdateJoinGame.class, event -> controller.on(this, event));
        view.addEventListener(ErrNewGame.class, event -> controller.on(this, event));
        view.addEventListener(ErrNickname.class, event -> controller.on(this, event));
        view.addEventListener(ErrAction.class, event -> controller.on(this, event));
        view.addEventListener(ErrActiveLeaderDiscarded.class, event -> controller.on(this, event));
        view.addEventListener(ErrBuyDevCard.class, event -> controller.on(this, event));
        view.addEventListener(ErrCardRequirements.class, event -> controller.on(this, event));
        view.addEventListener(ErrInitialChoice.class, event -> controller.on(this, event));
        view.addEventListener(ErrNoSuchEntity.class, event -> controller.on(this, event));
        view.addEventListener(ErrObjectNotOwned.class, event -> controller.on(this, event));
        view.addEventListener(ErrReplacedTransRecipe.class, event -> controller.on(this, event));
        view.addEventListener(ErrResourceReplacement.class, event -> controller.on(this, event));
        view.addEventListener(ErrResourceTransfer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateAction.class, event -> controller.on(this, event));
        view.addEventListener(UpdateActionToken.class, event -> controller.on(this, event));
        view.addEventListener(UpdateCurrentPlayer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateDevCardGrid.class, event -> controller.on(this, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> controller.on(this, event));
        view.addEventListener(UpdateFaithPoints.class, event -> controller.on(this, event));
        view.addEventListener(UpdateGame.class, event -> controller.on(this, event));
        view.addEventListener(UpdateGameEnd.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLastRound.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeader.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> controller.on(this, event));
        view.addEventListener(UpdateMarket.class, event -> controller.on(this, event));
        view.addEventListener(UpdatePlayer.class, event -> controller.on(this, event));
        view.addEventListener(UpdatePlayerStatus.class, event -> controller.on(this, event));
        view.addEventListener(UpdateResourceContainer.class, event -> controller.on(this, event));
        view.addEventListener(UpdateSetupDone.class, event -> controller.on(this, event));
        view.addEventListener(UpdateVaticanSection.class, event -> controller.on(this, event));
        view.addEventListener(UpdateVictoryPoints.class, event -> controller.on(this, event));
        view.addEventListener(UpdateLeadersHand.class, event -> controller.on(this, event));
    }

    private void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateBookedSeats.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateJoinGame.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNewGame.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNickname.class, event -> controller.on(this, event));
        view.removeEventListener(ErrAction.class, event -> controller.on(this, event));
        view.removeEventListener(ErrActiveLeaderDiscarded.class, event -> controller.on(this, event));
        view.removeEventListener(ErrBuyDevCard.class, event -> controller.on(this, event));
        view.removeEventListener(ErrCardRequirements.class, event -> controller.on(this, event));
        view.removeEventListener(ErrInitialChoice.class, event -> controller.on(this, event));
        view.removeEventListener(ErrNoSuchEntity.class, event -> controller.on(this, event));
        view.removeEventListener(ErrObjectNotOwned.class, event -> controller.on(this, event));
        view.removeEventListener(ErrReplacedTransRecipe.class, event -> controller.on(this, event));
        view.removeEventListener(ErrResourceReplacement.class, event -> controller.on(this, event));
        view.removeEventListener(ErrResourceTransfer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateAction.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateActionToken.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateCurrentPlayer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateDevCardGrid.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateGame.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateGameEnd.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLastRound.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeader.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateMarket.class, event -> controller.on(this, event));
        view.removeEventListener(UpdatePlayer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdatePlayerStatus.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateSetupDone.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateVictoryPoints.class, event -> controller.on(this, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> controller.on(this, event));
    }
}
