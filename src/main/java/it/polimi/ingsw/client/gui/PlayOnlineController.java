package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Alert;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class PlayOnlineController extends GuiController {
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    @FXML private TextField server;

    private NumberBinding maxScale;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }

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
                backStackPane.getChildren().add(
                    new Alert("Play Online", String.format("%s is not a valid pair IP:port.", server.getText()), maxScale)));
            return;
        } catch (NumberFormatException e) {
            Platform.runLater(() ->
                backStackPane.getChildren().add(
                    new Alert("Play Online", String.format("Port %s is not a valid port.", args[1]), maxScale)));
            return;
        }

        // Platform.runLater(() -> backStackPane.getChildren().add(new Alert("Play Online", String.format("Connecting to %s...", server.getText()))));

        try {
            gui.openOnlineClient(host, port);
        } catch (UnknownHostException e) {
            Platform.runLater(() -> 
                backStackPane.getChildren().add(
                    new Alert("Play Online", String.format("Don't know about host %s.", host), maxScale)));
            return;
        } catch (IOException e) {
            Platform.runLater(() -> 
                backStackPane.getChildren().add(
                    new Alert("Play Online", String.format("Couldn't get I/O for the connection to %s when creating the socket.", host), maxScale)));
            return;
        }

        gui.setRoot(getClass().getResource("/assets/gui/inputnickname.fxml"), (InputNicknameController controller) ->
                controller.setTitle("Play Online"));
    }

    @FXML
    private void handleBack() {
        Gui.getInstance().setRoot(getClass().getResource("/assets/gui/mainmenu.fxml"));
    }
}
