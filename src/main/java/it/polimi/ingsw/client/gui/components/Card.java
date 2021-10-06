package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * GUI abstract component representing a game card.
 */
public abstract class Card extends Pane {
    private static final double backgroundRadius = 16.6;
    @FXML
    protected Pane resourcePane;
    @FXML
    protected Text victoryPoints;
    protected Production production;
    protected CardRequirement requirement;

    /**
     * Class constructor.
     *
     * @param type the type of leader card, or the color of development card
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
     * Retrieves the name of the FXML file to search for.
     *
     * @return the name of the FXML file
     */
    protected abstract String getFXMLName();

    /**
     * Getter of the card's victory points.
     *
     * @return the victory points
     */
    public int getVictoryPoints() {
        return Integer.parseInt(victoryPoints.getText());
    }

    /**
     * Sets and displays the card bonus victory points.
     *
     * @param points the victory points given to the player by this card
     */
    public void setVictoryPoints(int points) {
        victoryPoints.setText(String.valueOf(points));
    }

    /**
     * Getter of the card requirements, either of resources or of cards.
     *
     * @return the card requirements
     */
    public CardRequirement getRequirement() {
        return requirement;
    }

    /**
     * Sets and displays the card's resource requirements.
     *
     * @param requirement the resource requirements of the card
     */
    public void setRequirement(ReducedResourceRequirement requirement) {
        this.requirement = new CardRequirement();
        this.requirement.setRequirements(requirement);
        this.getChildren().addAll(this.requirement);
        this.requirement.setLayoutX(15 + getWidth() * 0.12 / this.requirement.getChildren().size() / 1.5);
        this.requirement.setLayoutY(5);
    }

    /**
     * Getter of the card production.
     *
     * @return the card production
     */
    public Production getProduction() {
        return production;
    }

    /**
     * Sets and displays the card's production.
     *
     * @param prod the card's production
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        this.production = new Production();
        this.production.setProduction(prod);

        this.production.maxWidthProperty().bind(this.maxWidthProperty());
        this.production.maxHeightProperty().bind(this.maxHeightProperty());

        this.production.setLayoutX(20);

        this.getChildren().add(this.production);
    }
}
