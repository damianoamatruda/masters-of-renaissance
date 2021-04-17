package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.Optional;

/**
 * Leader card with the ability to produce resources.
 *
 * @see LeaderCard
 */
public class ProductionLeader extends LeaderCard {
    /** The production associated with the card. */
    private final Production production;

    /**
     * Class constructor.
     *
     * @param production    the production of the card
     * @param resource      the resource binding the card's ability effect. It is the resource the player needs to pay
     *                      in order to receive the production's output.
     * @param requirement   the requirement to be satisfied in order to enable the card
     * @param victoryPoints the amount of victory points associated with the card
     */
    public ProductionLeader(Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.production = production;
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
    @SuppressWarnings("unused")
    public ProductionLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        this(production, resource, requirement, victoryPoints);
    }

    @Override
    public Optional<Production> getProduction() {
        return isActive() ? Optional.of(production) : super.getProduction();
    }
}
