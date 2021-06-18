package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;

import java.util.Objects;

public class SButton extends Button {
    private static final String soundPath = "/assets/gui/29301__junggle__btn121.wav";
    private final AudioClip clip;

    /**
     *
     */
    public SButton() {
        clip = new AudioClip(Objects.requireNonNull(getClass().getResource(soundPath)).toExternalForm());
        this.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            double volume = Gui.getInstance().getSoundFxVolume();
            clip.setVolume(Gui.getInstance().getSoundFxVolume());
            if (volume > 0)
                clip.play();
        });
    }

    /**
     *
     * @param text
     */
    public SButton(String text) {
        this();
        this.setText(text);
    }
}
