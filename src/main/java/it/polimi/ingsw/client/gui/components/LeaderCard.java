package it.polimi.ingsw.client.gui.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

public class LeaderCard extends VBox {
    public Pane leaderCard;
    public Text leaderTypeText;
    public Text resourceTypeText;
    public Pane resourcePane;

    private Production prod;

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

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        this.prod = new Production();
        this.prod.setProduction(prod);

        this.prod.maxWidthProperty().bind(resourcePane.maxWidthProperty());
        this.prod.maxHeightProperty().bind(resourcePane.maxHeightProperty());

        resourcePane.getChildren().add(this.prod);
        resourcePane.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
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
