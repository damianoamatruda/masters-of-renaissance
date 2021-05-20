package it.polimi.ingsw.common.reducedmodel;

public class ReducedLeaderCard {
    private final int id;
    private final int victoryPoints;
    private final String resourceType;
    private final String leaderType;
    private final ReducedCardRequirement requirement;
    private boolean isActive;
    private boolean isDiscarded;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     */
    public ReducedLeaderCard(int id, int victoryPoints, String resourceType, String leaderType, boolean isActive,
            ReducedCardRequirement requirement) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.resourceType = resourceType;
        this.leaderType = leaderType;
        this.isActive = isActive;
        this.requirement = requirement;

        this.isDiscarded = false;
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
    
    public boolean isDiscarded() {
        return isDiscarded;
    }

    public void setDiscarded() {
        this.isDiscarded = true;
    }

    /**
     * @return the requirement of the card
     */
    public ReducedCardRequirement getRequirement() {
        return requirement;
    }
}
