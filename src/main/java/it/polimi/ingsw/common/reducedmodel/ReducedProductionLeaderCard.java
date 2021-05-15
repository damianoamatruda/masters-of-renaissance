package it.polimi.ingsw.common.reducedmodel;

public class ReducedProductionLeaderCard extends ReducedLeaderCard {
    private final int production;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     * @param production
     */
    public ReducedProductionLeaderCard(int id, int victoryPoints, String resourceType, String leaderType,
                                       boolean isActive, ReducedCardRequirement requirement, int production) {
        super(id, victoryPoints, resourceType, leaderType, isActive, requirement);
        this.production = production;
    }

    /**
     * @return the ID of the production of the card
     */
    public int getProduction() {
        return production;
    }
}
