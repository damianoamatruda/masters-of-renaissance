package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
        maxScale = Bindings.min(gui.getRoot().widthProperty().divide(Gui.realWidth),
                gui.getRoot().heightProperty().divide(Gui.realHeight));
        canvas.scaleXProperty().bind(maxScale);
        canvas.scaleYProperty().bind(maxScale);
    }

    /**
     * Tries to open a connection with the specified server.
     */
    @FXML
    private void handleServerInput() {
        Gui gui = Gui.getInstance();

        String[] args = server.getText().split(":");
        String host;
        int port;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            Platform.runLater(() ->
                    gui.getRoot().getChildren().add(
                            new Alert("Play Online", String.format("%s is not a valid pair IP:port.", server.getText()), maxScale)));
            return;
        } catch (NumberFormatException e) {
            Platform.runLater(() ->
                    gui.getRoot().getChildren().add(
                            new Alert("Play Online", String.format("Port %s is not a valid port.", args[1]), maxScale)));
            return;
        }

        // Platform.runLater(() -> gui.getRoot().getChildren().add(new Alert("Play Online", String.format("Connecting to %s...", server.getText()))));

        try {
            gui.getUi().openOnlineClient(host, port);
        } catch (UnknownHostException e) {
            Platform.runLater(() ->
                    gui.getRoot().getChildren().add(
                            new Alert("Play Online", String.format("Don't know about host %s.", host), maxScale)));
            return;
        } catch (IOException e) {
            Platform.runLater(() ->
                    gui.getRoot().getChildren().add(
                            new Alert("Play Online", "Could not connect to the server.", maxScale)));
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
        Gui.getInstance().setScene(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
