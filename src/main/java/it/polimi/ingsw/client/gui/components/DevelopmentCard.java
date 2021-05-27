package it.polimi.ingsw.client.gui.components;

import java.io.IOException;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class DevelopmentCard extends Card {
//    @FXML
//    Label level;
//    @FXML
//    Label color;
//    @FXML
//    HBox requirement;

    public DevelopmentCard(String color) {
        super(color);
    }

    public void setData(int level, DevCardColor color) {

//        this.color.setText(color.getName());
//        this.level.setText(Integer.toString(level));
    }

    public String getBackground(String color) {
        return "/assets/gui/devcardtemplates/" + color.toLowerCase() + "card.png";
    }

    public void setLevel(int level){
        // should put as many dots to the side flags as the level of the card
    }
}
