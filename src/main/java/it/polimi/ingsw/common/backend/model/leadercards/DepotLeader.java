package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceShelf;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.Optional;

/**
 * Leader card with the ability of storing resources, which will be considered as part of the player's warehouse.
 *
 * @see LeaderCard
 */
public class DepotLeader extends LeaderCard {
    /** The card's shelf. The resources that can be stored are only of one type, bound at creation. */
    private final ResourceShelf shelf;

    /**
     * Class constructor.
     * @param shelfSize     the maximum amount of resources the card can store
     * @param resource      the resource that binds the card's shelf's contents
     * @param requirement   the requirement to be satisfied to activate the card
     * @param victoryPoints the victory points associated with the card
     * @param id            the card identifier number
     */
    public DepotLeader(int shelfSize, ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(resource, requirement, victoryPoints, id);
        this.shelf = new ResourceShelf(resource, shelfSize);
    }

    @Override
    public Optional<ResourceShelf> getDepot() {
        return isActive() ? Optional.of(shelf) : super.getDepot();
    }

    @Override
    public ReducedLeaderCard reduce() {
        return new ReducedLeaderCard(getId(), getVictoryPoints(), getResource().getName(), getClass().getSimpleName(), isActive(), requirement.reduce(), shelf.getId(), -1, -1);
    }
}
