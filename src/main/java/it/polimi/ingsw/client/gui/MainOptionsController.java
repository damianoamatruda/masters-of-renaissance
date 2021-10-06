package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainOptionsController extends GuiController {
    @FXML
    private Pane canvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gui.setSceneScaling(canvas);
    }

    @FXML
    private void handleBack() {
        gui.setScene(getClass().getResource("/assets/gui/scenes/mainmenu.fxml"));
    }
}
