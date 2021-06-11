package it.polimi.ingsw.client.gui;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.minWidth),
                                              backStackPane.heightProperty().divide(Gui.minHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }

    @FXML
    private void handleServerInput() {
        Gui gui = Gui.getInstance();

        String[] args = server.getText().split(":");
        String host = "";
        int port = -1;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            System.out.printf("Connecting to %s...%n", server.getText());
            gui.openOnlineClient(host, port);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("%s is not a valid pair IP:port%n", server.getText());
            return;
        } catch (NumberFormatException e) {
            System.out.printf("Port %s is not a valid port%n", args[1]);
            return;
        } catch (UnknownHostException e) {
            System.out.printf("Don't know about host %s%n", host);
            return;
        } catch (IOException e) {
            System.out.printf("Couldn't get I/O for the connection to %s when creating the socket.%n", host);
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
