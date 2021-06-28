package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/** Gui component that represents a development card. */
public class DevelopmentCard extends Card {
    @FXML
    private Text levelLeft;
    @FXML
    private Text levelRight;

    /**
     * Class constructor.
     *
     * @param color the card color
     */
    public DevelopmentCard(String color) {
        super(color);
    }

    /**
     * Another class constructor.
     *
     * @param reducedDevCard the reduced model card
     */
    public DevelopmentCard(ReducedDevCard reducedDevCard) {
        super(reducedDevCard.getColor());
        reducedDevCard.getCost().ifPresent(this::setRequirement);
        setProduction(Gui.getInstance().getViewModel().getProduction(reducedDevCard.getProduction()).orElseThrow());
        setVictoryPoints(reducedDevCard.getVictoryPoints());
        setLevel(reducedDevCard.getLevel());
    }

    /**
     * Sets and displays the card level, as text.
     *
     * @param level the card level
     */
    public void setLevel(int level) {
        levelLeft.setText(String.valueOf(level));
        levelRight.setText(String.valueOf(level));
    }

    /**
     * Sets and displays the card bonus victory points.
     *
     * @param points the victory points given to the player by this card
     */
    public void setVictoryPoints(int points) {
        super.setVictoryPoints(points);
        victoryPoints.setLayoutX(getWidth() * 0.47);
        victoryPoints.setLayoutY(getHeight() * 0.91);
    }

    @Override
    protected String getFXMLName() {
        return "developmentcard";
    }

    /**
     * Sets and displays the card's resource requirements.
     *
     * @param requirement the resource requirements of the card
     */
    public void setRequirement(ReducedResourceRequirement requirement) {
        super.setRequirement(requirement);
        this.requirement.setLayoutX(15 + getWidth() * 0.36 / this.requirement.getChildren().size() / 1.5);
        this.requirement.setLayoutY(5);
    }

    /**
     * Sets and displays the card's production.
     *
     * @param prod the card's production
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.production.setLayoutX(getWidth() * 0.12);
        this.production.setLayoutY(getHeight() * 0.52);
    }
}
