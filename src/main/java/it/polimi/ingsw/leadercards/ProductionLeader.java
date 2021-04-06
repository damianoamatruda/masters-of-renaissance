package it.polimi.ingsw.leadercards;

import it.polimi.ingsw.CardRequirement;
import it.polimi.ingsw.Production;
import it.polimi.ingsw.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.resourcecontainers.Strongbox;
import it.polimi.ingsw.resourcetypes.ResourceType;

/**
 * Leader card with the ability to produce resources.
 */
public class ProductionLeader extends LeaderCard {
    /**
     * The production associated with the card.
     */
    private final Production<ResourceContainer, Strongbox> production;

    /**
     * Class constructor.
     *
     * @param production    the production of the card.
     * @param resource      the resource binding the card's ability effect.
     *                      It is the resource the player needs to pay in order to receive the production's output.
     * @param requirement   the requirement to be satisfied in order to enable the card.
     * @param victoryPoints the amount of victory points associated with the card.
     */
    public ProductionLeader(Production<ResourceContainer, Strongbox> production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.production = production;
    }

    @Override
    public Production<ResourceContainer, Strongbox> getProduction() { return production; }
}
