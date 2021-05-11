package it.polimi.ingsw.common.reducedmodel;

public class ReducedProductionLeaderCard extends ReducedLeaderCard {
    private final int productionId;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     * @param productionId
     */
    public ReducedProductionLeaderCard(int id, int victoryPoints, String resourceType, String leaderType,
            boolean isActive, ReducedCardRequirement requirement, int productionId) {
        super(id, victoryPoints, resourceType, leaderType, isActive, requirement);
        this.productionId = productionId;
    }

    /**
     * @return the productionId of the card
     */
    public int getProductionId() {
        return productionId;
    }
}
