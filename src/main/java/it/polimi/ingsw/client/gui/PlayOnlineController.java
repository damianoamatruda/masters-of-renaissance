package it.polimi.ingsw.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.UnknownHostException;

public class PlayOnlineController extends GuiController {
    @FXML
    private TextField server;

    public void handleServerInput() throws IOException {
        Gui gui = Gui.getInstance();

        String[] args = server.getText().split(":");
        String host = "";
        int port = -1;

        boolean connected = false;
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            System.out.printf("Connecting to %s...%n", server.getText());
            gui.startOnlineClient(host, port);
            connected = true;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("%s is not a valid pair IP:port%n", server.getText());
        } catch (NumberFormatException e) {
            System.out.printf("Port %s is not a valid port%n", args[1]);
        } catch (UnknownHostException e) {
            System.out.printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            System.out.printf("Couldn't get I/O for the connection to %s when creating the socket%n", host);
        }

        if (connected)
            gui.setRoot("inputnickname");
    }

    public void handleBack(ActionEvent actionEvent) throws IOException {
        Gui.getInstance().setRoot("mainmenu");
    }
}
