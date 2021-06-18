package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;

public abstract class Card extends Pane {
    private static final double backgroundRadius = 16.6;
    @FXML
    protected Pane resourcePane;
    @FXML
    protected Text victoryPoints;
    protected Production production;
    protected CardRequirement requirement;

    /**
     *
     * @param type
     */
    public Card(String type) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(String.format("/assets/gui/components/%s.fxml", getFXMLName())));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.getStyleClass().add(type.toLowerCase());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setWidth(getPrefWidth());
        setHeight(getPrefHeight());

        Rectangle r = new Rectangle(getWidth(), getHeight());
        r.setArcHeight(backgroundRadius);
        r.setArcWidth(backgroundRadius);
        widthProperty().addListener(p -> r.setWidth(getWidth()));
        heightProperty().addListener(p -> r.setWidth(getHeight()));
        setShape(r);
    }

    /**
     *
     * @return
     */
    protected abstract String getFXMLName();

    /**
     *
     * @param prod
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        this.production = new Production();
        this.production.setProduction(prod);

        this.production.maxWidthProperty().bind(this.maxWidthProperty());
        this.production.maxHeightProperty().bind(this.maxHeightProperty());

//        this.setAlignment(Pos.BOTTOM_CENTER);
        this.production.setLayoutX(20);

        this.getChildren().add(this.production);
//        this.setBorder(new Border(new BorderStroke(Color.BLACK,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    /**
     *
     * @param requirement
     */
    public void setRequirement(ReducedResourceRequirement requirement) {
        this.requirement = new CardRequirement();
        this.requirement.setRequirements(requirement);
        this.getChildren().addAll(this.requirement);
    }

    /**
     *
     * @param pts
     */
    public void setVictoryPoints(String pts) {
        victoryPoints.setText(pts);
    }

    /**
     *
     * @return
     */
    public String getVictoryPoints() {
        return victoryPoints.getText();
    }

    /**
     *
     * @return
     */
    public CardRequirement getRequirement() {
        return requirement;
    }

    /**
     *
     * @return
     */
    protected Production getProduction() {
        return production;
    }
}
