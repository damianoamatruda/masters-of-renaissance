package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.*;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Locale;

import javafx.scene.text.TextAlignment;

public class LeaderCard extends Card {
    public Pane leaderCard;
    public Text leaderTypeText;
    public Text resourceTypeText;

    public LeaderCard(String leaderType) {
        super(leaderType);
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

    public void setRequirement(ReducedDevCardRequirement requirement) {
        this.requirement = new CardRequirement();

        this.requirement.setRequirements(requirement);
        resourcePane.getChildren().addAll(this.requirement);
    }

    @Override
    protected String getFXMLName() {
        return "leadercard";
    }

    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
        victoryPoints.setLayoutX(79);
        this.victoryPoints.setLayoutY(154);
    }

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.prod.setLayoutY(185);
    }

    public void setZeroReplacement(ReducedResourceType type) {
        Resource res = new Resource();
        res.setResourceType(type.getName());

        res.setLayoutX(100);
        res.setLayoutY(195);

        res.setFitHeight(50);
        res.setFitWidth(50);

        this.getChildren().add(res);
    }

    public void setDiscount(ReducedResourceType type, int discount) {
        HBox bonus = new HBox();
        Resource res = new Resource();
        TextArea amount = new TextArea();
        amount.setText("-" + discount);
        amount.setLayoutX(100);
        amount.setLayoutY(175);
        //TODO text not showing

        res.setResourceType(type.getName());

        res.setFitHeight(35);
        res.setFitWidth(35);

        res.setLayoutX(120);
        res.setLayoutY(175);

        bonus.getChildren().add(amount);
        bonus.getChildren().add(res);

        this.getChildren().add(res);
    }

//    public void setDepotContent(ReducedResourceContainer content) {
//
//    }

}
