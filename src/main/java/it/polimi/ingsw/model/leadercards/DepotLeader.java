package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcecontainers.ResourceShelf;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

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
     *
     * @param shelfSize     the maximum amount of resources the card can store
     * @param resource      the resource that binds the card's shelf's contents
     * @param requirement   the requirement to be satisfied to activate the card
     * @param victoryPoints the victory points associated with the card
     */
    public DepotLeader(int shelfSize, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.shelf = new ResourceShelf(resource, shelfSize);
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
    public DepotLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        this(shelfSize, resource, requirement, victoryPoints);
    }

    @Override
    public Optional<ResourceShelf> getDepot() {
        return isActive() ? Optional.of(shelf) : super.getDepot();
    }
}
