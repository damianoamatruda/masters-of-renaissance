package it.polimi.ingsw.common.reducedmodel;

public class ReducedDiscountLeaderCard extends ReducedLeaderCard {
    private final int discount;

    /**
     * @param id
     * @param victoryPoints
     * @param resourceType
     * @param leaderType
     * @param isActive
     * @param requirement
     * @param discount
     */
    public ReducedDiscountLeaderCard(int id, int victoryPoints, String resourceType, String leaderType,
            boolean isActive, ReducedCardRequirement requirement, int discount) {
        super(id, victoryPoints, resourceType, leaderType, isActive, requirement);
        this.discount = discount;
    }

    /**
     * @return the discount of the card
     */
    public int getDiscount() {
        return discount;
    }
}
