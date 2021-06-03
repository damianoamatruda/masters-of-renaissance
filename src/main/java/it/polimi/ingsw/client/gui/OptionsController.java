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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Gui gui = Gui.getInstance();

        musicSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            gui.getMusicPlayer().ifPresent(p -> {
                p.setVolume(value);
                p.setMute(value == 0);
            });
            musicText.setText(String.format("%d%%", (int) (value * 100)));
        });
        gui.getMusicPlayer().ifPresent(p -> musicSlider.setValue(p.getVolume()));
    }

    @FXML
    private void handleBack() throws IOException {
        Gui gui = Gui.getInstance();
        gui.setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }

    @FXML
    private void handleMusicClick() {
        Gui.getInstance().getMusicPlayer().ifPresent(p -> {
            if (p.isMute())
                musicSlider.setValue(oldMusicVolume);
            else {
                oldMusicVolume = p.getVolume();
                musicSlider.setValue(0);
                p.setVolume(0);
                p.setMute(true);
            }
        });
    }

    @FXML
    private void handleSoundFxClick() {
    }
}
