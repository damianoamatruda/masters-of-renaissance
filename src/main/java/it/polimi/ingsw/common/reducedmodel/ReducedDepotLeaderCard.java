package it.polimi.ingsw.common.reducedmodel;

public class ReducedDepotLeaderCard extends ReducedLeaderCard {
    private final int containerId;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     * @param containerId
     */
    public ReducedDepotLeaderCard(int id, int victoryPoints, String resourceType, String leaderType, boolean isActive,
            ReducedCardRequirement requirement, int containerId) {
        super(id, victoryPoints, resourceType, leaderType, isActive, requirement);
        this.containerId = containerId;
    }

    /**
     * @return the containerId of the card
     */
    public int getContainerId() {
        return containerId;
    }
}
