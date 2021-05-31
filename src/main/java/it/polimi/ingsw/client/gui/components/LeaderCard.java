package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.*;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Map;

public class LeaderCard extends Card {
    public Pane leaderCard;
    public Text leaderTypeText;
    public Text resourceTypeText;

    private double height;
    private double width;

    private int leaderId;

    public LeaderCard(String leaderType) {
        super(leaderType);
        height = 251;   //TODO remove hardcoding
        width = 166;
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
        victoryPoints.setLayoutX(width / 2.1);
        this.victoryPoints.setLayoutY(height / 1.63);
    }

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.prod.setLayoutY(height / 1.3567);
    }

    public void setZeroReplacement(ReducedResourceType type) {
        Resource res = new Resource();
        res.setResourceType(type.getName());

        res.setLayoutX(width / 1.66);
        res.setLayoutY(height / 1.3);

        res.setFitHeight(height / 5);
        res.setFitWidth(height / 5);

        this.getChildren().add(res);
    }

    public void setDiscount(ReducedResourceType type, int discount) {
        HBox bonus = new HBox();
        Resource res = new Resource();
        TextArea amount = new TextArea();
        amount.setText("-" + discount);
        amount.setLayoutX(width / 1.66);
        amount.setLayoutY(height / 1.4342);
        //TODO text not showing

        res.setResourceType(type.getName());

        res.setFitHeight(height / 7.17);
        res.setFitWidth(height / 7.17);

        res.setLayoutX(width / 1.383);
        res.setLayoutY(height / 1.4342);

        bonus.getChildren().add(amount);
        bonus.getChildren().add(res);

        this.getChildren().add(res);
    }

    public void setDepotContent(ReducedResourceContainer container, String boundRes) {
        double x1 = width / 4.6, x2 = width / 1.7, y = height / 1.255;

        if(container == null) {
            fillDepot(x1, y, boundRes, true);
            fillDepot(x2, y, boundRes, true);
            return;
        }

        Map<String, Integer> content = container.getContent();

        String resourceType = content.keySet().stream().findAny().orElse(null);

        if(resourceType == null) {
            fillDepot(x1, y, boundRes, true);
            fillDepot(x2, y, boundRes, true);
            return;
        }

        int size = content.get(resourceType);
        fillDepot(x1, y, resourceType, false);
        fillDepot(x2, y, resourceType, size < 2);
    }

    private void fillDepot(double x, double y, String boundRes, boolean isEmpty) {
        ImageView img = new ImageView(new Image(getResourcePlaceholderPath(boundRes, isEmpty)));
        img.setX(x);
        img.setY(y);

        img.setFitHeight(height / 7.17);
        img.setFitWidth(height / 7.17);
        this.getChildren().add(img);
    }

    private String getResourcePlaceholderPath(String resourceType, boolean isEmpty) {
        if(isEmpty)
            return "/assets/gui/leadertemplates/" + resourceType.toLowerCase() + "depot.png";
        return "/assets/gui/resourcetypes/" + resourceType.toLowerCase() + ".png";
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }
}
