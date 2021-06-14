package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class Gui extends Application {
    private static final Logger LOGGER = Logger.getLogger(Gui.class.getName());
    private static final String initialSceneFxml = "/assets/gui/mainmenu.fxml";
    private static final String title = "Masters of Renaissance";
    /* Adjusting the ratio between 'real' and 'starting' sizes
       will result in the whole GUI changing its intrinsinc scaling.
       'real' values reflect the sizes set in the FXMLs.
       Therefore, if both 'starting' and 'real' sizes are set to 720p,
       the scale will result in a ratio of 1:1 for all sizes
       (the screen size of the components will be the same as their logical size). */
    static final double realWidth = 1280;
    static final double realHeight = 720;
    private static final double startWidth = 854;
    private static final double startHeight = 480;
    private static final double minWidth = 640;
    private static final double minHeight = 360;

    private static Gui instance = null;

    private final View view;
    private Network client;

    private Scene scene;
    private Stage stage;
    private GuiController controller;

    private final ViewModel viewModel;

    private InputStream gameConfigStream;
    private boolean offline;
    private MediaPlayer musicPlayer;
    private double soundFxVolume;

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
        Gui.instance = this;

        this.view = new View();
        setViewListeners();

        this.client = null;

        this.viewModel = new ViewModel();

        this.gameConfigStream = null;
        this.offline = false;
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
        scene = new Scene(root, startWidth, startHeight, false, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
        primaryStage.setTitle(title);
        primaryStage.show();
        this.stage = primaryStage;
    }

    @Override
    public void stop() {
        closeClient();
        view.close();
    }

    public void dispatch(Event event) {
        view.dispatch(event);
    }

    void setController(GuiController controller) {
        this.controller = controller;
    }

    void openOfflineClient() {
        closeClient();
        client = new OfflineClient(view, gameConfigStream);
        client.open();
        offline = true;
    }

    void openOnlineClient(String host, int port) throws IOException {
        closeClient();
        client = new OnlineClient(view, host, port);
        client.open();
        offline = false;
    }

    void closeClient() {
        if (client != null) {
            client.close();
            client = null;
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
            LOGGER.log(Level.SEVERE, "IOException when setting root", e);
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

    private void setViewListeners() {
        this.view.setResQuitEventListener(event -> controller.on(this, event));
        this.view.setUpdateBookedSeatsEventListener(event -> controller.on(this, event));
        this.view.setUpdateJoinGameEventListener(event -> controller.on(this, event));
        this.view.setErrNewGameEventListener(event -> controller.on(this, event));
        this.view.setErrNicknameEventListener(event -> controller.on(this, event));
        this.view.setErrActionEventListener(event -> controller.on(this, event));
        this.view.setErrActiveLeaderDiscardedEventListener(event -> controller.on(this, event));
        this.view.setErrBuyDevCardEventListener(event -> controller.on(this, event));
        this.view.setErrCardRequirementsEventListener(event -> controller.on(this, event));
        this.view.setErrInitialChoiceEventListener(event -> controller.on(this, event));
        this.view.setErrNoSuchEntityEventListener(event -> controller.on(this, event));
        this.view.setErrObjectNotOwnedEventListener(event -> controller.on(this, event));
        this.view.setErrReplacedTransRecipeEventListener(event -> controller.on(this, event));
        this.view.setErrResourceReplacementEventListener(event -> controller.on(this, event));
        this.view.setErrResourceTransferEventListener(event -> controller.on(this, event));
        this.view.setUpdateActionEventListener(event -> controller.on(this, event));
        this.view.setUpdateActionTokenEventListener(event -> controller.on(this, event));
        this.view.setUpdateCurrentPlayerEventListener(event -> controller.on(this, event));
        this.view.setUpdateDevCardGridEventListener(event -> controller.on(this, event));
        this.view.setUpdateDevCardSlotEventListener(event -> controller.on(this, event));
        this.view.setUpdateFaithPointsEventListener(event -> controller.on(this, event));
        this.view.setUpdateGameEventListener(event -> controller.on(this, event));
        this.view.setUpdateGameEndEventListener(event -> controller.on(this, event));
        this.view.setUpdateLastRoundEventListener(event -> controller.on(this, event));
        this.view.setUpdateActivateLeaderEventListener(event -> controller.on(this, event));
        this.view.setUpdateLeadersHandCountEventListener(event -> controller.on(this, event));
        this.view.setUpdateMarketEventListener(event -> controller.on(this, event));
        this.view.setUpdatePlayerEventListener(event -> controller.on(this, event));
        this.view.setUpdatePlayerStatusEventListener(event -> controller.on(this, event));
        this.view.setUpdateResourceContainerEventListener(event -> controller.on(this, event));
        this.view.setUpdateSetupDoneEventListener(event -> controller.on(this, event));
        this.view.setUpdateVaticanSectionEventListener(event -> controller.on(this, event));
        this.view.setUpdateVictoryPointsEventListener(event -> controller.on(this, event));
        this.view.setUpdateLeadersHandEventListener(event -> controller.on(this, event));
    }
}
