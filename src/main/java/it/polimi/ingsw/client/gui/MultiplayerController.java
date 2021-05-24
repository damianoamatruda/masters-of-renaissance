package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MultiplayerController {
    @FXML
    private TextField server;

    public void handleServerChoice() throws IOException {
        System.out.print("Input = " + server.getText());
    }
}
