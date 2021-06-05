package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class DevelopmentCard extends Card {
    private int height = 251;
    private int width = 166;
    @FXML
    private Text levelLeft;
    @FXML
    private Text levelRight;

    public DevelopmentCard(String color) {
        super(color);
    }

    public DevelopmentCard(ReducedDevCard card) {
        super(card.getColor());
        setRequirement(card.getCost());
        setProduction(Gui.getInstance().getViewModel().getProduction(card.getProduction()).orElseThrow());
        setVictoryPoints(card.getVictoryPoints()+"");
    }

    public void setLevel(int level){
        levelLeft.setText(level+"");
        levelRight.setText(level+"");
    }

    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
        victoryPoints.setLayoutX(width * 0.465);
        victoryPoints.setLayoutY(height * 0.91);
    }

    @Override
    protected String getFXMLName() {
        return "developmentcard";
    }

    public void setRequirement(ReducedResourceRequirement requirement) {
        super.setRequirement(requirement);
        this.requirement.setLayoutX(width * 0.12);
//        this.requirement.setAlignment(Pos.CENTER);
//        this.requirement.setLayoutY();
    }

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.prod.setLayoutX(width * 0.12);
        this.prod.setLayoutY(height * 0.518);
    }
}
