package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Map;

public class LeaderCard extends Card {
    @FXML
    private Pane leaderCard;
    @FXML
    private Text leaderTypeText;
    @FXML
    private Text resourceTypeText;
    
    private ReducedResourceContainer content;

    private int leaderId;

    private boolean isActivated;


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
        this.victoryPoints.setLayoutX(getWidth() * 0.47);
        this.victoryPoints.setLayoutY(getHeight() * 0.62);
    }

    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.prod.setLayoutY(getHeight() * 0.74);
    }

    public void setZeroReplacement(ReducedResourceType type) {
        Resource res = new Resource();
        res.setResourceType(type.getName());

        res.setLayoutX(getWidth() * 0.60);
        res.setLayoutY(getHeight() * 0.77);

        res.setFitHeight(getHeight() * 0.20);
        res.setFitWidth(getHeight() * 0.20);

        this.getChildren().add(res);
    }

    public void setDiscount(ReducedResourceType type, int discount) {
        HBox bonus = new HBox();
        Resource res = new Resource();
        TextArea amount = new TextArea();
        amount.setText("-" + discount);
        amount.setLayoutX(getWidth() * 0.60);
        amount.setLayoutY(getHeight() * 0.70);
        //TODO text not showing

        res.setResourceType(type.getName());

        res.setFitHeight(getHeight() * 0.14);
        res.setFitWidth(getHeight() * 0.14);

        res.setLayoutX(getWidth() * 0.72);
        res.setLayoutY(getHeight() * 0.70);

        bonus.getChildren().add(amount);
        bonus.getChildren().add(res);

        this.getChildren().add(res);
    }

    public ReducedResourceContainer getContainer() {
        return content;
    }

    public void setDepotContent(ReducedResourceContainer container, String boundRes) {
        content = container;

        double x1 = getWidth() * 0.22;
        double x2 = getWidth() * 0.59;
        double y = getHeight() * 0.80;

        if (container == null) {
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

        img.setFitHeight(getHeight() * 0.14);
        img.setFitWidth(getHeight() * 0.14);
        this.getChildren().add(img);
    }

    private String getResourcePlaceholderPath(String resourceType, boolean isEmpty) {
        if (isEmpty)
            return String.format("/assets/gui/leadertemplates/%sdepot.png", resourceType.toLowerCase());
        return String.format("/assets/gui/resourcetypes/%s.png", resourceType.toLowerCase());
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
