package it.polimi.ingsw.common.reducedmodel;

public class ReducedLeaderCard {
    private final int id;
    private final int victoryPoints;
    private final String resourceType;
    private final String leaderType;
    private final ReducedCardRequirement requirement;
    private boolean isActive;

    private final int containerId;
    private final int discount;
    private final int production;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     * @param containerId
     * @param discount
     * @param production
     */
    public ReducedLeaderCard(int id, int victoryPoints, String resourceType, String leaderType, boolean isActive,
            ReducedCardRequirement requirement,
            int containerId,
            int discount,
            int production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.resourceType = resourceType;
        this.leaderType = leaderType;
        this.isActive = isActive;
        this.requirement = requirement;
        
        this.containerId = containerId;
        this.discount = discount;
        this.production = production;
    }

    /**
     * @return the id of the card
     */
    public int getId() {
        return id;
    }

    /**
     * @return the victoryPoints of the card
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return the resourceType of the card
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * @return the leaderType of the card
     */
    public String getLeaderType() {
        return leaderType;
    }

    /**
     * @return the card isActive
     */
    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        this.isActive = true;
    }
    
    /**
     * @return the requirement of the card
     */
    public ReducedCardRequirement getRequirement() {
        return requirement;
    }

    /**
     * @return the containerId of the card
     */
    public int getContainerId() {
        return containerId;
    }

    /**
     * @return the discount of the card
     */
    public int getDiscount() {
        return discount;
    }

    /**
     * @return the ID of the production of the card
     */
    public int getProduction() {
        return production;
    }
}
