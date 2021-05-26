package it.polimi.ingsw.client.gui.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class LeaderCard extends VBox {
    public Pane leaderCard;
    public Text leaderTypeText;
    public Text resourceTypeText;

    public LeaderCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/leadercard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getLeaderType() {
        return leaderTypeProperty().get();
    }

    public void setLeaderType(String value) {
        leaderTypeProperty().set(value);
    }

    public StringProperty leaderTypeProperty() {
        return leaderTypeText.textProperty();
    }

    public String getResourceType() {
        return resourceTypeProperty().get();
    }

    public void setResourceType(String value) {
        resourceTypeProperty().set(value);
    }

    public StringProperty resourceTypeProperty() {
        return resourceTypeText.textProperty();
    }
}
