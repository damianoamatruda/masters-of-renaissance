package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.Options;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PauseMenuController extends GuiController {
    public BorderPane options;
    @FXML private StackPane backStackPane;
    @FXML private BorderPane bpane;
    private URL lastScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NumberBinding maxScale = Bindings.min(backStackPane.widthProperty().divide(Gui.realWidth),
                backStackPane.heightProperty().divide(Gui.realHeight));
        bpane.scaleXProperty().bind(maxScale);
        bpane.scaleYProperty().bind(maxScale);
    }

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
