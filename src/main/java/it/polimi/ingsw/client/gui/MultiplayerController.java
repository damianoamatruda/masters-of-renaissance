package it.polimi.ingsw.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.UnknownHostException;

public class MultiplayerController extends GuiController {
    @FXML
    private TextField server;

    public void handleServerInput() throws IOException {
        String host = "127.0.0.1";
        int port = 1234;

        boolean connected = true;
        try {
            Gui.startNetworkClient(host, port);
        } catch (UnknownHostException e) {
            connected = false;
            System.out.printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            connected = false;
            System.out.printf("Couldn't get I/O for the connection to %s when creating the socket%n", host);
        }

        if (connected)
            Gui.setRoot("inputnickname");
        else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleBack(ActionEvent actionEvent) throws IOException {
        Gui.setRoot("mainmenu");
    }
}
