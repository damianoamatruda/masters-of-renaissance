package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.Optional;

/**
 * Leader card with the ability to produce resources.
 *
 * @see LeaderCard
 */
public class ProductionLeader extends LeaderCard {
    /** The production associated with the card. */
    private final ResourceTransactionRecipe production;

    /**
     * Class constructor.
     *
     * @param production    the production of the card
     * @param resource      the resource binding the card's ability effect. It is the resource the player needs to pay
     *                      in order to receive the production's output.
     * @param requirement   the requirement to be satisfied in order to enable the card
     * @param victoryPoints the amount of victory points associated with the card
     * @param id            the card id
     */
    public ProductionLeader(ResourceTransactionRecipe production, ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(resource, requirement, victoryPoints, id);
        this.production = production;
    }

    @Override
    public Optional<ResourceTransactionRecipe> getProduction(boolean isFactoryAsking) {
        if (isFactoryAsking)
            return Optional.of(production);

        return isActive() ? Optional.of(production) : super.getProduction(isFactoryAsking);
    }

    @Override
    public ReducedLeaderCard reduce() {
        return new ReducedLeaderCard(
            getId(), getVictoryPoints(),
                new ReducedResourceType(getResource().getName(), getResource().getColorValue(), getResource().isStorable()),
                getClass().getSimpleName(), isActive(), requirement.reduceDR(), requirement.reduceRR(), -1, -1, production.getId());
    }
}
