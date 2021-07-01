package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.PauseMenu;
import it.polimi.ingsw.client.gui.components.PauseOptions;
import it.polimi.ingsw.client.gui.components.SButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class Gui extends Application {
    private static final Logger LOGGER = Logger.getLogger(Gui.class.getName());

    /* Adjusting the ratio between 'real' and 'starting' sizes
       will result in the whole GUI changing its intrinsinc scaling.
       'real' values reflect the sizes set in the FXMLs.
       Therefore, if both 'starting' and 'real' sizes are set to 720p,
       the scale will result in a ratio of 1:1 for all sizes
       (the screen size of the components will be the same as their logical size). */
    private static final double startWidth = 854;
    private static final double startHeight = 480;
    private static final double minWidth = 640;
    private static final double minHeight = 360;
    static final double realWidth = 1280;
    static final double realHeight = 720;

    private static final String initialSceneFxml = "/assets/gui/mainmenu.fxml";
    private static final String mainStylesheetCss = "/assets/gui/index.css";
    private static final String title = "Masters of Renaissance";
    private static Gui instance = null;
    private final Ui ui;
    private Stage stage;
    private Pane root;
    private URL currentScene;
    private Pane pauseMenu;
    private Pane pauseOptions;
    private MediaPlayer musicPlayer;
    private double soundFxVolume;

    public Gui() {
        Gui.instance = this;
        this.ui = new Ui();
        this.currentScene = null;
        this.pauseMenu = null;
        this.pauseOptions = null;
        this.musicPlayer = null;
        this.soundFxVolume = 1;
    }

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

    @Override
    public void start(Stage primaryStage) throws IOException {
        root = new StackPane();
        root.getStylesheets().add(mainStylesheetCss);
        Parent scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(initialSceneFxml)));
        root.getChildren().add(scene);
        setSceneScaling(scene);
        stage = primaryStage;
        stage.setScene(new Scene(root, startWidth, startHeight, false, SceneAntialiasing.BALANCED));
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setTitle(title);
        stage.show();
    }

    public void stop() {
        ui.stop();
    }

    void quit() {
        Platform.exit();
        System.exit(0);
    }

    void setController(GuiController controller) {
        ui.setController(controller);
    }

    public ViewModel getViewModel() {
        return ui.getViewModel();
    }

    public Ui getUi() {
        return ui;
    }

    void setScene(URL fxml, Consumer<? extends GuiController> callback) {
        this.currentScene = fxml;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            Parent scene = fxmlLoader.load();
            setSceneScaling(scene);
            if (callback != null)
                callback.accept(fxmlLoader.getController());
            Platform.runLater(() -> root.getChildren().set(0, scene));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException when setting root", e);
            throw new RuntimeException(e);
        }
    }

    void setScene(URL fxml) {
        setScene(fxml, null);
    }

    /**
     * Reloads the current screen, executing a callback when finished loading.
     *
     * @param title    the title of the Alert to show before reloading the GUI's root
     * @param content  the content message of the Alert to show before reloading the GUI's root
     * @param callback the callback to execute when setting the new root element
     */
    void reloadScene(String title, String content, Consumer<? extends GuiController> callback) {
        /* Other ways of doing this were explored:
            - a reloadRoot() method set as the Alert's callback
                Discarded, since every children.add would get very verbose
            - a reloadRoot(Alert) method
                Discarded, since the Alert would need a callback anyways (see this implementation)
            - a reloadRoot(Callback) that would call setRoot(currentRoot, callback)
                Discarded, since the runLater methods wouldn't get executed in order and
                the Alert would be visible for only a split second */
        Platform.runLater(() -> addToOverlay(new Alert(title, content, () -> setScene(currentScene, callback))));
    }

    /**
     * Reloads the current screen.
     *
     * @param title   the title of the Alert to show before reloading the GUI's root
     * @param content the content message of the Alert to show before reloading the GUI's root
     */
    void reloadScene(String title, String content) {
        reloadScene(title, content, null);
    }

    public void setSceneScaling(Parent scene) {
        NumberBinding maxScale = Bindings.min(
                root.widthProperty().divide(realWidth),
                root.heightProperty().divide(realHeight));
        scene.scaleXProperty().bind(maxScale);
        scene.scaleYProperty().bind(maxScale);
    }

    public Stage getStage() {
        return stage;
    }

    public Pane getRoot() {
        return root;
    }

    public Optional<MediaPlayer> getMusicPlayer() {
        return Optional.ofNullable(musicPlayer);
    }

    public void setPauseHandler(Pane scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                handlePause();
        });
    }

    public void addPauseButton(Pane scene) {
        Button pause = new SButton("Pause");
        pause.addEventHandler(ActionEvent.ACTION, actionEvent -> handlePause());
        scene.getChildren().add(pause);
        AnchorPane.setBottomAnchor(pause, 10.0);
        AnchorPane.setLeftAnchor(pause, 10.0);
    }

    private void handlePause() {
        if (pauseOptions == null)
            pauseOptions = new PauseOptions();
        if (pauseMenu == null)
            pauseMenu = new PauseMenu(pauseOptions);
        if (!root.getChildren().contains(pauseMenu) && !root.getChildren().contains(pauseOptions)) {
            addToOverlay(pauseMenu);
        } else {
            removeFromOverlay(pauseMenu);
            removeFromOverlay(pauseOptions);
        }
    }

    public void addToOverlay(Pane child) {
        root.getChildren().add(child);
    }

    public void removeFromOverlay(Pane child) {
        root.getChildren().remove(child);
    }

    public double getSoundFxVolume() {
        return soundFxVolume;
    }

    public void setMusicPlayer(MediaPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public void setSoundFxVolume(double soundFxVolume) {
        this.soundFxVolume = soundFxVolume;
    }
}
