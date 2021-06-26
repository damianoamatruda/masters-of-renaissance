package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Options;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/** Gui controller that manages the main menu. */
public class MainMenuController extends GuiController {
    private static final String musicPath = "/assets/gui/Wonderland - 320bit.mp3";

    @FXML
    private BorderPane canvas;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(gui.getRoot().widthProperty().divide(Gui.realWidth),
                gui.getRoot().heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);

        Gui gui = Gui.getInstance();

        if (gui.getMusicPlayer().isEmpty()) {
            new Thread(new Task<>() {
                @Override
                protected Void call() {
                    Media media = new Media(Objects.requireNonNull(getClass().getResource(musicPath)).toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.play();
                    gui.setMusicPlayer(player);
                    return null;
                }
            }).start();
        }
    }

    /**
     * Handles choice of playing offline Solo.
     */
    @FXML
    private void handlePlayOffline() {
        Gui gui = Gui.getInstance();
        gui.getUi().openOfflineClient();
        gui.setScene(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Offline"));
    }

    /**
     * Handles choice of playing with a server.
     */
    @FXML
    private void handlePlayOnline() {
        Gui.getInstance().setScene(getClass().getResource("/assets/gui/playonline.fxml"));
    }

    /**
     * Opens the options screen.
     */
    @FXML
    private void handleOptions() {
        Options options = new Options(maxScale);
        options.setConfigContainer(true);
        gui.getRoot().getChildren().add(options);
    }

    /**
     * Soft quits the application.
     */
    @FXML
    private void handleQuit() {
        Gui.getInstance().quit();
    }
}
