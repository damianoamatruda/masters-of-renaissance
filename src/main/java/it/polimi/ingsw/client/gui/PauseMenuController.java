package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Options;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.net.URL;

public class PauseMenuController extends GuiController {
    public BorderPane options;
    @FXML private StackPane backStackPane;
    private URL lastScene;

    @FXML
    private void handleBack() {
        Gui.getInstance().setRoot(lastScene);
    }

    @FXML
    private void handleOptions() {
        Options options = new Options();
        options.setConfigContainer(false);
        backStackPane.getChildren().add(options);
    }

    @FXML
    private void handleQuit() {
        Gui.getInstance().dispatch(new ReqQuit());
    }

    public void setLastScene(URL location) {
        this.lastScene = location;
    }
}
