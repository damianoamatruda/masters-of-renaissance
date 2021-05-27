package it.polimi.ingsw.client.gui;

import java.io.IOException;

import it.polimi.ingsw.client.gui.components.DevelopmentCard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class PlaygroundController extends Pane {
    

    @FXML
    private void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/developmentcard.fxml"));
//        fxmlLoader.setController(new DevelopmentCard());
        this.getChildren().add(fxmlLoader.load());
    }
}
