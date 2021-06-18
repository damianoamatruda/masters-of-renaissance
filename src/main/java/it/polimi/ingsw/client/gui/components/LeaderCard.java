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

/** Gui component that represents a leader card. */
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

    /**
     * Class constructor.
     *
     * @param leaderType the type of leader card
     */
    public LeaderCard(String leaderType) {
        super(leaderType);
        this.leaderType = leaderType;
    }

    /**
     * Getter of the leader type.
     *
     * @return the type of leader card
     */
    public String getLeaderType() {
        return leaderType;
    }

    public void setLeaderType(String value) {
//        leaderTypeProperty().set(value);
    }

    public StringProperty leaderTypeProperty() {
        return leaderTypeText.textProperty();
    }

    /**
     * Getter of the leader's target resource type.
     *
     * @return the target resource type of the leader card
     */
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

    /**
     * Sets and displays the development card requirements.
     *
     * @param requirement the development card requirements of this card
     */
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
     * Sets and displays the card bonus victory points.
     *
     * @param pts the victory points given to the player by this card
     */
    public void setVictoryPoints(String pts) {
        super.setVictoryPoints(pts);
        this.victoryPoints.setLayoutX(getWidth() * 0.47);
        this.victoryPoints.setLayoutY(getHeight() * 0.62);
    }

    /**
     * Sets and displays the production included in this leader (valid for production leaders).
     *
     * @param prod the cached production recipe
     */
    public void setProduction(ReducedResourceTransactionRecipe prod) {
        super.setProduction(prod);
        this.production.setLayoutY(getHeight() * 0.74);
    }

    /**
     * Sets and displays the available Zero replacement (valid for leaders of type zero leader).
     *
     * @param type  the resource type to replace the Zero
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
     * Sets and displays the leader discount (valid for discount leaders).
     *
     * @param type      the discounted resource type
     * @param discount  the discount in unities
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
     * Getter of the leader depots (valid for depot leaders).
     *
     * @return the cached model resource container
     */
    public ReducedResourceContainer getContainer() {
        return content;
    }

    /**
     * Sets and displays the depots content (valid for depot leaders).
     *
     * @param container the resource container of the leader card
     * @param boundRes  the depots' bound resource type
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
     * Displays the depot content (using a placeholder if empty).
     *
     * @param x         the horizontal coordinate for placing the ImageView
     * @param y         the vertical coordinate for placing the ImageView
     * @param boundRes  the depot's bound resource type
     * @param isEmpty   true if the leader depot is unoccupied
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
     * Getter of the path to the PNG of the depot content.
     *
     * @param resourceType  the depot's bound resource type
     * @param isEmpty       true if the leader depot is unoccupied
     * @return  the path to the PNG resource representing the depot content
     */
    private String getResourcePlaceholderPath(String resourceType, boolean isEmpty) {
        if (isEmpty)
            return String.format("/assets/gui/leadertemplates/%sdepot.png", resourceType.toLowerCase());
        return String.format("/assets/gui/resourcetypes/%s.png", resourceType.toLowerCase());
    }

    /**
     * Getter of the card ID.
     *
     * @return the card ID
     */
    public int getLeaderId() {
        return leaderId;
    }

    /**
     * Sets the ID of the card.
     *
     * @param leaderId the card's ID
     */
    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    /**
     * Getter of the activation status.
     *
     * @return true if leader is active
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Changes activation status.
     *
     * @param activated the updated activation status of the card.
     */
    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
