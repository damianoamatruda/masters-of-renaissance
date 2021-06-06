package it.polimi.ingsw.client.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController extends GuiController {
    private static final String musicPath = "/assets/gui/Wonderland - 320bit.mp3";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    @FXML
    private void handlePlayOffline() {
        Gui gui = Gui.getInstance();
        gui.startOfflineClient();
        gui.setRoot(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Offline"));
    }

    @FXML
    private void handlePlayOnline() {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playonline.fxml"));
    }

    @FXML
    private void handleOptions() {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/options.fxml"));
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().quit();
    }
}
