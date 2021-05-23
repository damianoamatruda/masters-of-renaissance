package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MultiPlayerController {
    @FXML
    private TextField server;

    public void handleServerChoice() throws IOException {
        System.out.print("Input = " + server.getText());
//        Gui.setRoot("");
//      Send request of connection to server
    }
}
