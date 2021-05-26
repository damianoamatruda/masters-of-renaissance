package it.polimi.ingsw.client.gui.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.util.Locale;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.scene.text.TextAlignment;

public class LeaderCard extends VBox {
    public Pane leaderCard;
    public Text leaderTypeText;
    public Text resourceTypeText;
    public Pane resourcePane;
    public Text victoryPoints;

    private Production prod;

    public LeaderCard(String leaderType) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(/*getBackground(leaderType)*/"/assets/gui/components/leadercard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.setStyle("-fx-background-image: url('" + getBackground(leaderType) + "');" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 166 251;");

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

        this.setStyle("-fx-alignment: ");

        resourcePane.getChildren().add(this.prod);
        resourcePane.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    public String getLeaderType() {
        return leaderTypeProperty().get();
    }

    public void setLeaderType(String value) {
//        leaderTypeProperty().set(value);
    }

    public StringProperty leaderTypeProperty() {
        return leaderTypeText.textProperty();
    }

    public String getResourceType() {
        return resourceTypeProperty().get();
    }

    public void setResourceType(String value) {
//        resourceTypeProperty().set(value);
//        resourceTypeText.setTextAlignment(TextAlignment.JUSTIFY);
    }

    public StringProperty resourceTypeProperty() {
        return resourceTypeText.textProperty();
    }

    public String getBackground(String leaderType) {
        return "/assets/gui/leadertemplates/" + leaderType.toLowerCase() + ".png";
    }

    public void setVictoryPoints(String pts) {
        victoryPoints.setText(pts);
    }

    public String getVictoryPoints() {
        return victoryPoints.getText();
    }
}
