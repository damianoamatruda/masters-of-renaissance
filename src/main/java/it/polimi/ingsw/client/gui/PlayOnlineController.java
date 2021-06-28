package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/** Gui controller used to prompt a destination server, and open a connection with it. */
public class PlayOnlineController extends GuiController {
    @FXML
    private BorderPane canvas;
    @FXML
    private TextField server;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
    }

    /**
     * Tries to open a connection with the specified server.
     */
    @FXML
    private void handleServerInput() {
        String[] args = server.getText().split(":", 2);
        String host;
        int port;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (server.getText().isBlank())
                gui.reloadScene("Connection error", "Server address is blank.");
            else
                gui.reloadScene("Connection error", String.format("%s is not a valid pair IP:port.", server.getText()));
            return;
        } catch (NumberFormatException e) {
            gui.reloadScene("Connection error", String.format("Port %s is not a valid port.", args[1]));
            return;
        }

        // Platform.runLater(() -> gui.getRoot().getChildren().add(new Alert("Play Online", String.format("Connecting to %s...", server.getText()))));

        try {
            gui.getUi().openOnlineClient(host, port);
        } catch (UnknownHostException e) {
            gui.reloadScene("Connection error", String.format("Do not know about host %s.", host));
            return;
        } catch (IOException e) {
            gui.reloadScene("Connection error", "Could not connect to the server.");
            return;
        }

        gui.setScene(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Online"));
    }

    /**
     * Handles going back to main menu.
     */
    @FXML
    private void handleBack() {
        gui.setScene(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
