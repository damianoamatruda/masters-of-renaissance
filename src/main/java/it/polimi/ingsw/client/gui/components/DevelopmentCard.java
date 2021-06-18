package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class DevelopmentCard extends Card {
    @FXML
    private Text levelLeft;
    @FXML
    private Text levelRight;

    /**
     *
     * @param color
     */
    public DevelopmentCard(String color) {
        super(color);
    }

    /**
     *
     * @param card
     */
    public DevelopmentCard(ReducedDevCard card) {
        super(card.getColor());
        card.getCost().ifPresent(cost -> setRequirement(cost));
        setProduction(Gui.getInstance().getViewModel().getProduction(card.getProduction()).orElseThrow());
        setVictoryPoints(card.getVictoryPoints()+"");
    }

    /**
     *
     * @param level
     */
    public void setLevel(int level){
        levelLeft.setText(level+"");
        levelRight.setText(level+"");
    }

    /**
     *
     * @param pts
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
     *
     * @param requirement
     */
    public void setRequirement(ReducedResourceRequirement requirement) {
        super.setRequirement(requirement);
        this.requirement.setLayoutX(getWidth() * 0.12);
//        this.requirement.setAlignment(Pos.CENTER);
//        this.requirement.setLayoutY();
    }

    /**
     *
     * @param prod
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.production.setLayoutX(getWidth() * 0.12);
        this.production.setLayoutY(getHeight() * 0.52);
    }
}
