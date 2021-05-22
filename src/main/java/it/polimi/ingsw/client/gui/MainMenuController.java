package it.polimi.ingsw.client.gui;

import javafx.application.Platform;

import java.io.IOException;

public class MainMenuController {
    public void handleSinglePlayer() throws IOException {
        System.out.println("Launching singleplayer...");
        // Gui.setRoot("singleplayer");
    }

    public void handleMultiPlayer() throws IOException {
        System.out.println("Launching multiplayer...");
        // Gui.setRoot("multiplayer");
    }

    public void handleOptions() throws IOException {
        System.out.println("Launching options...");
        // Gui.setRoot("options");
    }

    public void handleQuit() throws IOException {
        System.out.println("Quitting...");
        Platform.exit();
        System.exit(0);
    }
}
