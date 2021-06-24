package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
     * @param card the reduced model card
     */
    public DevelopmentCard(ReducedDevCard card) {
        super(card.getColor());
        card.getCost().ifPresent(this::setRequirement);
        setProduction(Gui.getInstance().getViewModel().getProduction(card.getProduction()).orElseThrow());
        setVictoryPoints(card.getVictoryPoints()+"");
    }

    /**
     * Sets and displays the card level, as text.
     *
     * @param level the card level
     */
    public void setLevel(int level){
        levelLeft.setText(level+"");
        levelRight.setText(level+"");
    }

    /**
     * Sets and displays the card bonus victory points.
     *
     * @param pts the victory points given to the player by this card
     */
    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
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
        this.requirement.setLayoutX(15 + getWidth() * 0.36 / ((double) this.requirement.getChildren().size()) / 1.5);
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
