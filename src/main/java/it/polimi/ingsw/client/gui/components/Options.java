package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * GUI component representing the Options screen.
 */
public class Options extends BorderPane {
    @FXML
    private BorderPane main;
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
    private EventHandler<ActionEvent> onBack = this::handleBack;
    private double oldMusicVolume;

    /**
     * Overlay displaying the game's settings
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

        if (gui.getUi().getGameConfig().isEmpty())
            resetConfigButton.setDisable(true);
    }

    /**
     * @param value
     * @return
     */
    private static String getPercentage(double value) {
        return String.format("%d%%", (int) (value * 100));
    }

    /**
     *
     */
    private void setHandlers() {
        musicText.setOnMouseClicked(e -> handleMusicClick());
        soundFxText.setOnMouseClicked(e -> handleSoundFxClick());
        customConfigButton.setOnAction(e -> handleConfig());
        resetConfigButton.setOnAction(e -> handleResetConfig());
        backButton.setOnAction(e -> onBack.handle(e));
    }

    /**
     *
     */
    @FXML
    private void handleBack(ActionEvent actionEvent) {
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
                gui.getUi().loadGameConfigStream(new FileInputStream(gameConfigFile));
                resetConfigButton.setDisable(false);
            } catch (IOException e) {
                Platform.runLater(() -> gui.addToOverlay(
                        new Alert(title, String.format("Could not gain access to file %s.%n", gameConfigFile.getPath()))));
            }
        }
    }

    /**
     * Resets the configuration to the included file
     */
    @FXML
    private void handleResetConfig() {
        Gui.getInstance().getUi().resetGameConfig();
        resetConfigButton.setDisable(true);
    }

    public boolean getConfigContainer() {
        return ((Pane) configContainer.getParent()).getChildren().contains(configContainer);
    }

    /**
     * @param isPresent
     */
    public void setConfigContainer(boolean isPresent) {
        if (!isPresent)
            ((Pane) configContainer.getParent()).getChildren().remove(configContainer);
    }

    public EventHandler<ActionEvent> getOnBack() {
        return onBack;
    }

    public void setOnBack(EventHandler<ActionEvent> eventHandler) {
        onBack = eventHandler;
    }
}
