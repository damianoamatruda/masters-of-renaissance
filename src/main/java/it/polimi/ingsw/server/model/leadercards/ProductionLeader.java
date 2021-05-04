package it.polimi.ingsw.server.model.leadercards;

import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

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
     *  @param production   the production of the card
     * @param resource      the resource binding the card's ability effect. It is the resource the player needs to pay
     *                      in order to receive the production's output.
     * @param requirement   the requirement to be satisfied in order to enable the card
     * @param victoryPoints the amount of victory points associated with the card
     * @param id            the card id
     */
    public ProductionLeader(Production production, ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(resource, requirement, victoryPoints, id);
        this.production = production;
    }

    @Override
    public Optional<Production> getProduction() {
        return isActive() ? Optional.of(production) : super.getProduction();
    }
}
