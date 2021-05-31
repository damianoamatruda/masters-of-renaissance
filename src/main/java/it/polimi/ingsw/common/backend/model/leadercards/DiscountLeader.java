package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Leader card with the ability to give a discount when buying a development card.
 *
 * @see LeaderCard
 */
public class DiscountLeader extends LeaderCard {
    /** The amount of the leader's resource to be subtracted from the development card's cost. */
    private final int discount;

    /**
     * Class constructor.
     * @param discount      the amount of resources to be subtracted when applying the ability.
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied in order to enable the card.
     * @param victoryPoints the amount of victory points associated with the card.
     * @param id            the card id
     */
    public DiscountLeader(int discount, ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(resource, requirement, victoryPoints, id);
        this.discount = discount;
    }

    @Override
    public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> cost) {
        if (cost == null) return null;

        if (!isActive()) return super.getDevCardCost(cost);

        Map<ResourceType, Integer> discountedCost = new HashMap<>(cost);

        discountedCost.computeIfPresent(this.getResource(), (r, oldCost) -> oldCost - discount);

        return discountedCost;
    }

    @Override
    public ReducedLeaderCard reduce() {
        return new ReducedLeaderCard(getId(), getVictoryPoints(),
                new ReducedResourceType(getResource().getName(), getResource().getColorValue(), getResource().isStorable()),
                getClass().getSimpleName(), isActive(), requirement.reduceDR(), requirement.reduceRR(), -1, discount, -1);
    }
}
