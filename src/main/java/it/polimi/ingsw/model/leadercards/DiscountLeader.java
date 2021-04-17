package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

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
     *
     * @param discount      the amount of resources to be subtracted when applying the ability.
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied in order to enable the card.
     * @param victoryPoints the amount of victory points associated with the card.
     */
    @SuppressWarnings("unused")
    public DiscountLeader(int discount, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.discount = discount;
    }

    /**
     * Complete class constructor needed for automatic construction.
     *
     * @param discount      the amount of resources to be subtracted when applying the ability (unused in this case)
     * @param shelfSize     the maximum amount of resources the card can store (unused in this case)
     * @param production    the production of the card (unused in this case)
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied for card activation
     * @param victoryPoints the victory points associated with the card
     */
    public DiscountLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        this(discount, resource, requirement, victoryPoints);
    }

    @Override
    public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> cost) {
        if (cost == null) return null;

        if (!isActive()) return super.getDevCardCost(cost);

        Map<ResourceType, Integer> discountedCost = new HashMap<>(cost);

        discountedCost.computeIfPresent(this.getResource(), (r, oldCost) -> oldCost - discount);

        return discountedCost;
    }
}
