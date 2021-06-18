package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Map;

public class LeaderCard extends Card {
    private final String leaderType;
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
        this.leaderType = leaderType;
    }

    public String getLeaderType() {
        return leaderType;
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

    /**
     *
     * @param pts
     */
    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
        this.victoryPoints.setLayoutX(getWidth() * 0.47);
        this.victoryPoints.setLayoutY(getHeight() * 0.62);
    }

    /**
     *
     * @param prod
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.production.setLayoutY(getHeight() * 0.74);
    }

    /**
     *
     * @param type
     */
    public void setZeroReplacement(String type) {
        Resource res = new Resource();
        res.setResourceType(type);

        res.setLayoutX(getWidth() * 0.60);
        res.setLayoutY(getHeight() * 0.77);

        res.setFitHeight(getHeight() * 0.20);
        res.setFitWidth(getHeight() * 0.20);

        this.getChildren().add(res);
    }

    /**
     *
     * @param type
     * @param discount
     */
    public void setDiscount(String type, int discount) {
        HBox bonus = new HBox(-3);
        bonus.setAlignment(Pos.CENTER);

        Text amount = new Text();
        amount.setText(String.valueOf(-1 * discount));
        
        Resource res = new Resource();
        res.setResourceType(type);

        res.setFitHeight(getHeight() * 0.14);
        res.setFitWidth(getHeight() * 0.14);

        bonus.setLayoutX(getWidth() * 0.67);
        bonus.setLayoutY(getHeight() * 0.70);

        bonus.getChildren().add(amount);
        bonus.getChildren().add(res);

        this.getChildren().add(bonus);
    }

    /**
     *
     * @return
     */
    public ReducedResourceContainer getContainer() {
        return content;
    }

    /**
     *
     * @param container
     * @param boundRes
     */
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

        content.keySet().stream().findAny().ifPresentOrElse(r -> {
            int size = content.get(r);
            fillDepot(x1, y, r, false);
            fillDepot(x2, y, r, size < 2);
        }, () -> {
            fillDepot(x1, y, boundRes, true);
            fillDepot(x2, y, boundRes, true);
        });

    }

    /**
     *
     * @param x
     * @param y
     * @param boundRes
     * @param isEmpty
     */
    private void fillDepot(double x, double y, String boundRes, boolean isEmpty) {
        ImageView img = new ImageView(new Image(getResourcePlaceholderPath(boundRes, isEmpty)));
        img.setX(x);
        img.setY(y);

        img.setFitHeight(getHeight() * 0.14);
        img.setFitWidth(getHeight() * 0.14);
        this.getChildren().add(img);
    }

    /**
     *
     * @param resourceType
     * @param isEmpty
     * @return
     */
    private String getResourcePlaceholderPath(String resourceType, boolean isEmpty) {
        if (isEmpty)
            return String.format("/assets/gui/leadertemplates/%sdepot.png", resourceType.toLowerCase());
        return String.format("/assets/gui/resourcetypes/%s.png", resourceType.toLowerCase());
    }

    /**
     *
     * @return
     */
    public int getLeaderId() {
        return leaderId;
    }

    /**
     *
     * @param leaderId
     */
    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    /**
     *
     * @return
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     *
     * @param activated
     */
    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
