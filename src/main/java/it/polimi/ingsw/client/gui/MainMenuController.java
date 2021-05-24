package it.polimi.ingsw.client.gui;

import java.io.IOException;

public class MainMenuController extends GuiController {
    public void handleSinglePlayer() throws IOException {
        Gui.startLocalClient();
        Gui.setRoot("inputnickname");
    }

    public void handleMultiplayer() throws IOException {
        Gui.setRoot("multiplayer");
    }

    public void handleOptions() throws IOException {
        System.out.println("Launching Options...");
    }

    public void handleQuit() throws IOException {
        Gui.quit();
    }
}
