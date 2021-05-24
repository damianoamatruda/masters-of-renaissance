package it.polimi.ingsw.client.gui;

import java.io.IOException;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class DevelopmentCardController extends Pane {
    @FXML
    Label level;
    @FXML
    Label color;
    @FXML
    HBox requirement;

    public DevelopmentCardController(int level, DevCardColor color) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("developmentcard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.color.setText(color.getName());
        this.level.setText(Integer.toString(level));
    }
}
