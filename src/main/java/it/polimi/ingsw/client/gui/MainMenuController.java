package it.polimi.ingsw.client.gui;

import java.io.IOException;

public class MainMenuController {

    public void handleSinglePlayer() throws IOException {
        Gui.setRoot("singleplayer");
    }

    public void handleMultiPlayer() throws IOException {
        Gui.setRoot("multiplayer");
    }

    public void handleOptions() throws IOException {
        Gui.setRoot("options");
    }

    public void handleQuit() throws IOException {
//        Gui.setRoot("singleplayer");
    }
}
