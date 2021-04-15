package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcecontainers.ResourceShelf;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

/**
 * Leader card with the ability of storing resources, which will be considered as part of the player's warehouse.
 *
 * @see LeaderCard
 */
public class DepotLeader extends LeaderCard {
    /**
     * The card's shelf. The resources that can be stored are only of one type, bound at creation.
     */
    private final ResourceShelf shelf;

    /**
     * Class constructor.
     *
     * @param discount      the amount of resources to be subtracted when applying the ability (unused in this case)
     * @param shelfSize     the maximum amount of resources the card can store
     * @param production    the production of the card (unused in this case)
     * @param resource      the resource that binds the card's shelf's contents
     * @param requirement   the requirement to be satisfied to activate the card
     * @param victoryPoints the victory points associated with the card
     */
    public DepotLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.shelf = new ResourceShelf(resource, shelfSize);
    }

    @Override
    public ResourceShelf getDepot() {
        if (isActive()) return shelf;
        else return super.getDepot();
    }
}
