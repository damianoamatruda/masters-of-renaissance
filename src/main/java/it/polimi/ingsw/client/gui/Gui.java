package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
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

    private final Ui ui;

    private Scene scene;
    private Stage stage;
    private GuiController controller;

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

        this.ui = new Ui();

        setViewListeners();

        this.musicPlayer = null;
        this.soundFxVolume = 1;
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

    public void stop() {
        ui.stop();
    }

    void quit() {
        Platform.exit();
        System.exit(0);
    }

    void setController(GuiController controller) {
        this.controller = controller;
    }

    public ViewModel getViewModel() {
        return ui.getViewModel();
    }

    public Ui getUi() {
        return ui;
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
        ui.getView().setResQuitEventListener(event -> controller.on(event));
        ui.getView().setUpdateBookedSeatsEventListener(event -> controller.on(event));
        ui.getView().setUpdateJoinGameEventListener(event -> controller.on(event));
        ui.getView().setErrNewGameEventListener(event -> controller.on(event));
        ui.getView().setErrNicknameEventListener(event -> controller.on(event));
        ui.getView().setErrActionEventListener(event -> controller.on(event));
        ui.getView().setErrActiveLeaderDiscardedEventListener(event -> controller.on(event));
        ui.getView().setErrBuyDevCardEventListener(event -> controller.on(event));
        ui.getView().setErrCardRequirementsEventListener(event -> controller.on(event));
        ui.getView().setErrInitialChoiceEventListener(event -> controller.on(event));
        ui.getView().setErrNoSuchEntityEventListener(event -> controller.on(event));
        ui.getView().setErrObjectNotOwnedEventListener(event -> controller.on(event));
        ui.getView().setErrReplacedTransRecipeEventListener(event -> controller.on(event));
        ui.getView().setErrResourceReplacementEventListener(event -> controller.on(event));
        ui.getView().setErrResourceTransferEventListener(event -> controller.on(event));
        ui.getView().setUpdateActionEventListener(event -> controller.on(event));
        ui.getView().setUpdateActionTokenEventListener(event -> controller.on(event));
        ui.getView().setUpdateCurrentPlayerEventListener(event -> controller.on(event));
        ui.getView().setUpdateDevCardGridEventListener(event -> controller.on(event));
        ui.getView().setUpdateDevCardSlotEventListener(event -> controller.on(event));
        ui.getView().setUpdateFaithPointsEventListener(event -> controller.on(event));
        ui.getView().setUpdateGameEventListener(event -> controller.on(event));
        ui.getView().setUpdateGameEndEventListener(event -> controller.on(event));
        ui.getView().setUpdateLastRoundEventListener(event -> controller.on(event));
        ui.getView().setUpdateActivateLeaderEventListener(event -> controller.on(event));
        ui.getView().setUpdateLeadersHandCountEventListener(event -> controller.on(event));
        ui.getView().setUpdateMarketEventListener(event -> controller.on(event));
        ui.getView().setUpdatePlayerEventListener(event -> controller.on(event));
        ui.getView().setUpdatePlayerStatusEventListener(event -> controller.on(event));
        ui.getView().setUpdateResourceContainerEventListener(event -> controller.on(event));
        ui.getView().setUpdateSetupDoneEventListener(event -> controller.on(event));
        ui.getView().setUpdateVaticanSectionEventListener(event -> controller.on(event));
        ui.getView().setUpdateVictoryPointsEventListener(event -> controller.on(event));
        ui.getView().setUpdateLeadersHandEventListener(event -> controller.on(event));
    }
}
