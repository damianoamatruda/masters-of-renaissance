package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;

import java.util.Map;

public class DiscountLeader extends LeaderCard {
    /**
     * The amount of the leader's resource to be subtracted from the development card's cost.
     */
    private final int discount;

    /**
     * Class constructor.
     *
     * @param discount      the amount of resources to be subtracted when applying the ability.
     * @param resource      the resource bound to the card.
     *                      The card's ability is restricted to acting on this resource type only.
     * @param requirement   the requirement to be satisfied in order to enable the card.
     * @param victoryPoints the amount of victory points associated with the card.
     */
    public DiscountLeader(int discount, ResourceType resource, LeaderCardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.discount = discount;
    }

    @Override
    public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> resources) {
        //TODO implement
        return null;
    }
}
