package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

public class DevelopmentCard extends Card {
//    @FXML private Label level;
//    @FXML private Label color;
//    @FXML private HBox requirement;

    public DevelopmentCard(String color) {
        super(color);
    }

    public void setData(int level, DevCardColor color) {
//        this.color.setText(color.getName());
//        this.level.setText(Integer.toString(level));
    }

    public String getBackground(String color) {
        return String.format("/assets/gui/devcardtemplates/%scard.png", color.toLowerCase());
    }

    public void setLevel(int level){
        // should put as many dots to the side flags as the level of the card
    }

    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
        victoryPoints.setLayoutX(76);
        victoryPoints.setLayoutY(228);
    }

    @Override
    protected String getFXMLName() {
        return "developmentcard";
    }

    public void setRequirement(ReducedResourceRequirement requirement) {
        super.setRequirement(requirement);
        this.requirement.setLayoutX(20);
//        this.requirement.setAlignment(Pos.CENTER);
//        this.requirement.setLayoutY();
    }

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.prod.setLayoutX(20);
        this.prod.setLayoutY(130);
    }
}
