package it.polimi.ingsw.client.gui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController extends GuiController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        if (!Gui.getInstance().isMusicPlaying()) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/gui/Wonderland - 320bit.mp3")).toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.play();
                    Gui.getInstance().getMediaPlayers().add(player);
                    Gui.getInstance().setMusicPlaying(true);
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void handlePlayOffline() throws IOException {
        Gui gui = Gui.getInstance();
        gui.startOfflineClient();
        gui.setRoot(getClass().getResource("/assets/gui/inputnickname.fxml"));
    }

    public void handlePlayOnline() throws IOException {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playonline.fxml"));
    }

    public void handleOptions(ActionEvent actionEvent) throws IOException {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/options.fxml"));
    }

    public void handleQuit() throws IOException {
        Gui.getInstance().quit();
    }
}
