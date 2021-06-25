package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.client.gui.components.Alert;
import it.polimi.ingsw.client.gui.components.PauseMenu;
import it.polimi.ingsw.client.gui.components.SButton;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    private static final String title = "Masters of Renaissance";
    private static Gui instance = null;
    private final Ui ui;
    private Scene scene;
    private Stage stage;
    private MediaPlayer musicPlayer;
    private double soundFxVolume;

    private URL currentRoot;

    public Gui() {
        Gui.instance = this;
        this.ui = new Ui();
        this.musicPlayer = null;
        this.soundFxVolume = 1;

        this.currentRoot = null;
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
        ui.setController(controller);
    }

    GuiController getController() {
        return (GuiController) ui.getController();
    }

    public ViewModel getViewModel() {
        return ui.getViewModel();
    }

    public Ui getUi() {
        return ui;
    }

    void setRoot(URL fxml, Consumer<?> callback) {
        this.currentRoot = fxml;
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

    /**
     * Reloads the current screen, executing a callback when finished loading.
     * 
     * @param title   the title of the Alert to show before reloading the GUI's root
     * @param content the content message of the Alert to show before reloading the GUI's root
     * @param callback the callback to execute when setting the new root element
     */
    void reloadRoot(String title, String content, Consumer<?> callback) {
        /* Other ways of doing this were explored:
            - a reloadRoot() method set as the Alert's callback
                Discarded, since every children.add would get very verbose
            - a reloadRoot(Alert) method
                Discarded, since the Alert would need a callback anyways (see this implementation)
            - a reloadRoot(Callback) that would call setRoot(currentRoot, callback)
                Discarded, since the runLater methods wouldn't get executed in order and
                the Alert would be visible for only a split second */
        Platform.runLater(() -> {
            getController().getRootElement().getChildren().add(
                new Alert(title, content, getController().getMaxScale(), () -> setRoot(currentRoot, callback)));
        });
    }

    /**
     * Reloads the current screen.
     * 
     * @param title   the title of the Alert to show before reloading the GUI's root
     * @param content the content message of the Alert to show before reloading the GUI's root
     */
    void reloadRoot(String title, String content) {
        reloadRoot(title, content, null);
    }

    public Stage getStage() {
        return stage;
    }

    public Optional<MediaPlayer> getMusicPlayer() {
        return Optional.ofNullable(musicPlayer);
    }

    public static void setPauseHandlers(StackPane backStackPane, AnchorPane canvas, NumberBinding maxScale) {
        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (backStackPane.getChildren().size() == 1)
                    backStackPane.getChildren().add(new PauseMenu(maxScale));
                else backStackPane.getChildren().remove(backStackPane.getChildren().size() - 1);
            }
        });

        Button pause = new SButton();
        pause.setText("Pause");
        pause.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            backStackPane.getChildren().add(new PauseMenu(maxScale));
        });
        canvas.getChildren().add(pause);
        AnchorPane.setBottomAnchor(pause, 10.0);
        AnchorPane.setLeftAnchor(pause, 10.0);
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
