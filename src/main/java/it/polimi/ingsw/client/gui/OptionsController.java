package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController extends GuiController {
    @FXML
    private Slider musicSlider;
    @FXML
    private Slider soundFxSlider;
    @FXML
    private Text musicText;
    @FXML
    private Text soundFxText;
    private double oldMusicVolume;
    private double oldSoundFxVolume;

    private static String getPercentage(double value) {
        return String.format("%d%%", (int) (value * 100));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui gui = Gui.getInstance();

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
    }

    @FXML
    private void handleBack() throws IOException {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

    @FXML
    private void handleMusicClick() {
        if (musicSlider.getValue() == 0)
            musicSlider.setValue(oldMusicVolume);
        else {
            oldMusicVolume = musicSlider.getValue();
            musicSlider.setValue(0);
        }
    }

    @FXML
    private void handleSoundFxClick() {
        if (soundFxSlider.getValue() == 0)
            soundFxSlider.setValue(oldMusicVolume);
        else {
            oldMusicVolume = soundFxSlider.getValue();
            soundFxSlider.setValue(0);
        }
    }
}
