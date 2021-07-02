package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;

import java.util.HashMap;
import java.util.Map;

/**
 * Leader card with the ability to give a discount when buying a development card.
 *
 * @see LeaderCard
 */
public class DiscountLeader extends LeaderCard {
    /** The quantity of the leader's resources to be subtracted from the development card's cost. */
    private final int discount;

    /**
     * Class constructor.
     *
     * @param discount      the quantity of resources to be subtracted when applying the ability.
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied in order to enable the card.
     * @param victoryPoints the quantity of victory points associated with the card.
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

        discountedCost.computeIfPresent(this.getResource(), (r, oldCost) -> oldCost - discount > 0 ? oldCost - discount : null);

        return discountedCost;
    }

    @Override
    public ReducedLeaderCard reduce() {
        return new ReducedLeaderCard(getId(), getVictoryPoints(), getResource().getName(), LeaderType.DISCOUNT,
                isActive(), requirement.reduceDR(), requirement.reduceRR(), -1, discount, -1);
    }
}
