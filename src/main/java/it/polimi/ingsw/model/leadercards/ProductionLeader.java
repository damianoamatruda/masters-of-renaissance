package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

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
     * @param discount      the amount of resources to be subtracted when applying the ability (unused in this case)
     * @param shelfSize     the maximum amount of resources the card can store (unused in this case)
     * @param production    the production of the card
     * @param resource      the resource binding the card's ability effect. It is the resource the player needs to pay
     *                      in order to receive the production's output.
     * @param requirement   the requirement to be satisfied in order to enable the card
     * @param victoryPoints the amount of victory points associated with the card
     */
    public ProductionLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.production = production;
    }

    @Override
    public Production getProduction() {
        if (isActive()) return production;
        else return super.getProduction();
    }
}
