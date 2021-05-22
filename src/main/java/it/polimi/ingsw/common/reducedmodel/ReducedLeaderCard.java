package it.polimi.ingsw.common.reducedmodel;

public class ReducedLeaderCard extends ReducedCard {
    private final String resourceType;
    private final String leaderType;
    private final ReducedDevCardRequirement devCardRequirement;
    private final ReducedResourceRequirement resourceRequirement;
    private boolean isActive;

    private final int containerId;
    private final int discount;

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
        super(id, victoryPoints, production);
        this.resourceType = resourceType;
        this.leaderType = leaderType;
        this.isActive = isActive;
        this.devCardRequirement = devCardRequirement;
        this.resourceRequirement = resourceRequirement;
        
        this.containerId = containerId;
        this.discount = discount;
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

}
