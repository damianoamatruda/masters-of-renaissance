package it.polimi.ingsw.client.gui;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController extends GuiController {
    Media media;
    MediaPlayer player;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        if (!Gui.getInstance().isMusicPlaying()) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    media = new Media(Objects.requireNonNull(getClass().getResource("/assets/gui/Wonderland - 320bit.mp3")).toString());
                    player = new MediaPlayer(media);
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.play();
                    Gui.getInstance().setMusicPlaying(true);
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void handleSinglePlayer() throws IOException {
        Gui gui = Gui.getInstance();
        gui.startLocalClient();
        gui.setRoot("inputnickname");
    }

    public void handleMultiplayer() throws IOException {
        Gui.getInstance().setRoot("multiplayer");
    }

    public void handleOptions() throws IOException {
        System.out.println("Launching Options...");
    }

    public void handleQuit() throws IOException {
        Gui.getInstance().quit();
    }
}
