package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * GUI component that represents a leader card.
 */
public class LeaderCard extends Card {
    private final LeaderType leaderType;
    private final String resourceType;
    @FXML
    private Pane leaderCard;
    @FXML
    private Text leaderTypeText;
    @FXML
    private Text resourceTypeText;

    private ReducedResourceContainer content;

    private int leaderId;

    private boolean isActive;
    private Shelf depot;

    /**
     * Class constructor.
     *
     * @param leaderType   the type of leader card
     * @param resourceType the target resource type of the leader card
     */
    public LeaderCard(LeaderType leaderType, String resourceType) {
        super(leaderType != null ? String.format("%sleader", leaderType.toString().toLowerCase()) : "backleader");
        this.leaderType = leaderType;
        this.resourceType = resourceType;
    }

    public LeaderCard(ReducedLeaderCard reducedLeaderCard) {
        this(reducedLeaderCard.getLeaderType(), reducedLeaderCard.getResourceType());
        setLeaderId(reducedLeaderCard.getId());
        setVictoryPoints(reducedLeaderCard.getVictoryPoints());
        setActive(reducedLeaderCard.isActive());
        reducedLeaderCard.getResourceRequirement().ifPresent(this::setRequirement);
        reducedLeaderCard.getDevCardRequirement().ifPresent(this::setRequirement);
    }

    /**
     * Getter of the leader type.
     *
     * @return the type of leader card
     */
    public LeaderType getLeaderType() {
        return leaderType;
    }

    /**
     * Getter of the leader's target resource type.
     *
     * @return the target resource type of the leader card
     */
    public String getResourceType() {
        return resourceType;
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
        resourcePane.setLayoutX(15 + getWidth() * 0.12 / this.requirement.getChildren().size() / 1.5);
        resourcePane.setLayoutY(5);
    }

    @Override
    protected String getFXMLName() {
        return "leadercard";
    }

    /**
     * Sets and displays the card bonus victory points.
     *
     * @param points the victory points given to the player by this card
     */
    public void setVictoryPoints(int points) {
        super.setVictoryPoints(points);
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
     * @param type the resource type to replace the Zero
     */
    public void setZeroReplacement(String type) {
        Resource res = new Resource(type);

        res.setLayoutX(getWidth() * 0.60);
        res.setLayoutY(getHeight() * 0.77);

        res.setFitHeight(getHeight() * 0.20);
        res.setFitWidth(getHeight() * 0.20);

        this.getChildren().add(res);
    }

    /**
     * Sets and displays the leader discount (valid for discount leaders).
     *
     * @param type     the discounted resource type
     * @param discount the discount in unities
     */
    public void setDiscount(String type, int discount) {
        HBox bonus = new HBox(-3);
        bonus.setAlignment(Pos.CENTER);

        Text quantity = new Text(String.valueOf(-1 * discount));

        Resource res = new Resource(type);

        res.setFitHeight(getHeight() * 0.14);
        res.setFitWidth(getHeight() * 0.14);

        bonus.setLayoutX(getWidth() * 0.67);
        bonus.setLayoutY(getHeight() * 0.70);

        bonus.getChildren().add(quantity);
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

    public Shelf getGuiDepot() {
        return depot;
    }

    /**
     * Sets and displays the depots content (valid for depot leaders).
     *
     * @param container the resource container of the leader card
     * @param boundRes  the depots' bound resource type
     */
    public void setDepotContent(ReducedResourceContainer container, String boundRes, boolean wantsDnD) {
        content = container;

        Shelf shelf = new Shelf(container, 48, 100, true);
        shelf.setLayoutY(193);
        shelf.setLayoutX(12);
        this.getChildren().add(shelf);

        if (container.getContent().size() == 0)
            shelf.setPlaceholder(boundRes);
        else
            for (int i = 0; i < container.getContent().get(boundRes); i++) {
                if (wantsDnD)
                    shelf.addResourceDraggable(boundRes);
                else
                    shelf.addResource(boundRes);
            }

        this.depot = shelf;

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
    public boolean isActive() {
        return isActive;
    }

    /**
     * Changes activation status.
     *
     * @param active the updated activation status of the card.
     */
    public void setActive(boolean active) {
        isActive = active;
    }
}
