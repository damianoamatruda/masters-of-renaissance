package it.polimi.ingsw.client.gui;

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
        gui.setSceneScaling(canvas);

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
        gui.getUi().openOfflineClient();
        gui.setScene(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Offline"));
    }

    /**
     * Handles choice of playing with a server.
     */
    @FXML
    private void handlePlayOnline() {
        gui.setScene(getClass().getResource("/assets/gui/playonline.fxml"));
    }

    /**
     * Opens the options screen.
     */
    @FXML
    private void handleOptions() {
        gui.setScene(getClass().getResource("/assets/gui/mainoptions.fxml"));
    }

    /**
     * Soft quits the application.
     */
    @FXML
    private void handleQuit() {
        gui.quit();
    }
}
