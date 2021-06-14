package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Options;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController extends GuiController {
    private static final String musicPath = "/assets/gui/Wonderland - 320bit.mp3";

    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    
    private NumberBinding maxScale;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);

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
        backStackPane.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bpane.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    @FXML
    private void handlePlayOffline() {
        Gui gui = Gui.getInstance();
        gui.getUi().openOfflineClient();
        gui.setRoot(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Offline"));
    }

    @FXML
    private void handlePlayOnline() {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/playonline.fxml"));
    }

    @FXML
    private void handleOptions() {
        Options options = new Options(maxScale);
        options.setConfigContainer(true);
        backStackPane.getChildren().add(options);
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().quit();
    }
}
