package it.polimi.ingsw.common.reducedmodel;

public class ReducedLeaderCard {
    private final int id;
    private final int victoryPoints;
    private final String resourceType;
    private final String leaderType;
    private final ReducedDevCardRequirement devCardRequirement;
    private final ReducedResourceRequirement resourceRequirement;
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
     * @param containerId
     * @param discount
     * @param production
     */
    public ReducedLeaderCard(int id, int victoryPoints, String resourceType, String leaderType, boolean isActive,
            ReducedDevCardRequirement devCardRequirement,
            ReducedResourceRequirement resourceRequirement,
            int containerId,
            int discount,
            int production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.resourceType = resourceType;
        this.leaderType = leaderType;
        this.isActive = isActive;
        this.devCardRequirement = devCardRequirement;
        this.resourceRequirement = resourceRequirement;
        
        this.containerId = containerId;
        this.discount = discount;
        this.production = production;
    }

    /**
     * @return the resourceRequirement
     */
    public ReducedResourceRequirement getResourceRequirement() {
        return resourceRequirement;
    }

    /**
     * @return the devCardRequirement
     */
    public ReducedDevCardRequirement getDevCardRequirement() {
        return devCardRequirement;
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
