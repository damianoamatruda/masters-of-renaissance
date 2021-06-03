package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public abstract class Card extends Pane {
    @FXML
    protected Pane resourcePane;
    @FXML
    protected Text victoryPoints;
    protected Production prod;
    protected CardRequirement requirement;

    protected Card(String type) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(String.format("/assets/gui/components/%s.fxml", getFXMLName())));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.getStyleClass().add(type.toLowerCase());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected abstract String getFXMLName();

    public abstract String getBackground(String type);

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        this.prod = new Production();
        this.prod.setProduction(prod);

        this.prod.maxWidthProperty().bind(this.maxWidthProperty());
        this.prod.maxHeightProperty().bind(this.maxHeightProperty());

//        this.setAlignment(Pos.BOTTOM_CENTER);
        this.prod.setLayoutX(20);

        this.getChildren().add(this.prod);
//        this.setBorder(new Border(new BorderStroke(Color.BLACK,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    public void setRequirement(ReducedResourceRequirement requirement) {
        this.requirement = new CardRequirement();

        this.requirement.setRequirements(requirement);
        this.getChildren().addAll(this.requirement);
    }

    public void setVictoryPoints(String pts) {
        victoryPoints.setText(pts);
    }

    public String getVictoryPoints() {
        return victoryPoints.getText();
    }

    public CardRequirement getRequirement() {
        return requirement;
    }

}
