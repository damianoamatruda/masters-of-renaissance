package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;

import java.util.Objects;

/** Gui component representing a button with sound on click. */
public class SButton extends Button {
    private static final String soundPath = "/assets/gui/29301__junggle__btn121.wav";
    private final AudioClip clip;

    /**
     * Class constructor.
     */
    public SButton() {
        Gui gui = Gui.getInstance();

        clip = new AudioClip(Objects.requireNonNull(getClass().getResource(soundPath)).toExternalForm());
        this.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) -> {
            double volume = gui.getSoundFxVolume();
            clip.setVolume(gui.getSoundFxVolume());
            if (volume > 0)
                clip.play();
        });
    }

    /**
     * Class constructor.
     *
     * @param text the text of the button
     */
    public SButton(String text) {
        this();
        this.setText(text);
    }
}
