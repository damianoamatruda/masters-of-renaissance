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

    public void handlePlayground() throws IOException {
        Gui.setRoot("playground");
    }

    public void handleQuit() throws IOException {
        Gui.quit();
    }
}
