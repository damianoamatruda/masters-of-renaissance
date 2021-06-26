package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/** Gui component representing the Options screen. */
public class Options extends BorderPane {
    @FXML
    private VBox window;
    @FXML
    private VBox configContainer;
    @FXML
    private Slider musicSlider;
    @FXML
    private Slider soundFxSlider;
    @FXML
    private Text musicText;
    @FXML
    private Text soundFxText;
    @FXML
    private SButton resetConfigButton;
    @FXML
    private SButton customConfigButton;
    @FXML
    private SButton backButton;
    private double oldMusicVolume;

    /**
     *
     * @param value
     * @return
     */
    private static String getPercentage(double value) {
        return String.format("%d%%", (int) (value * 100));
    }

    /**
     * TODO
     */
    public Options() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/options.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Gui gui = Gui.getInstance();

        gui.setSceneScaling(this);

        setHandlers();

        musicSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            gui.getMusicPlayer().ifPresent(p -> {
                p.setVolume(value);
                p.setMute(value == 0);
            });
            musicText.setText(getPercentage(value));
        });
        gui.getMusicPlayer().ifPresent(p -> {
            musicSlider.setValue(p.getVolume());
            musicText.setText(getPercentage(p.getVolume()));
        });

        soundFxSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            gui.setSoundFxVolume(value);
            soundFxText.setText(getPercentage(value));
        });
        soundFxSlider.setValue(gui.getSoundFxVolume());
        soundFxText.setText(getPercentage(gui.getSoundFxVolume()));

        if (gui.getUi().getGameConfigStream().isEmpty())
            resetConfigButton.setDisable(true);

        window.setBorder(new Border(new BorderStroke(Color.rgb(214, 150, 0),
            BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));
    }

    /**
     *
     */
    private void setHandlers() {
        musicText.setOnMouseClicked(e -> handleMusicClick());
        soundFxText.setOnMouseClicked(e -> handleSoundFxClick());
        customConfigButton.setOnAction(e -> handleConfig());
        resetConfigButton.setOnAction(e -> handleResetConfig());
        backButton.setOnAction(e -> handleBack());
    }

    /**
     *
     */
    @FXML
    private void handleBack() {
        ((Pane) this.getParent()).getChildren().remove(this);
    }

    /**
     *
     */
    @FXML
    private void handleMusicClick() {
        if (musicSlider.getValue() == 0)
            musicSlider.setValue(oldMusicVolume);
        else {
            oldMusicVolume = musicSlider.getValue();
            musicSlider.setValue(0);
        }
    }

    /**
     *
     */
    @FXML
    private void handleSoundFxClick() {
        if (soundFxSlider.getValue() == 0)
            soundFxSlider.setValue(oldMusicVolume);
        else {
            oldMusicVolume = soundFxSlider.getValue();
            soundFxSlider.setValue(0);
        }
    }

    /**
     *
     */
    @FXML
    private void handleConfig() {
        Gui gui = Gui.getInstance();

        String title = "Open custom config.json";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File gameConfigFile = fileChooser.showOpenDialog(gui.getStage());
        if (gameConfigFile != null) {
            try {
                gui.getUi().setGameConfigStream(new FileInputStream(gameConfigFile));
                resetConfigButton.setDisable(false);
            } catch (FileNotFoundException e) {
                Platform.runLater(() ->
                        ((Pane) this.getParent()).getChildren().add(
                                new Alert(title, String.format("Couldn't gain access to file %s.%n", gameConfigFile.getPath()))));
            }
        }
    }

    /**
     *
     */
    @FXML
    private void handleResetConfig() {
        Gui.getInstance().getUi().setGameConfigStream(null);
        resetConfigButton.setDisable(true);
    }

    /**
     *
     * @param isPresent
     */
    public void setConfigContainer(boolean isPresent) {
        if (!isPresent) {
            ((VBox) configContainer.getParent()).getChildren().remove(configContainer);
        }
    }
}
