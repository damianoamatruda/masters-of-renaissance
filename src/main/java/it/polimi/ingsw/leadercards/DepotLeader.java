package it.polimi.ingsw.leadercards;

import it.polimi.ingsw.CardRequirement;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.ResourceShelf;

/**
 * LeaderCard with the ability of storing resources,
 * which will be considered as part of the player's warehouse.
 */
public class DepotLeader extends LeaderCard {
    /**
     * The card's shelf. The resources that can be stored are only of one type, bound at creation.
     */
    private final ResourceShelf shelf;

    /**
     * Class constructor.
     *
     * @param shelfSize     the maximum amount of resources the card can store.
     * @param resource      the resource that binds the card's shelf's contents.
     * @param requirement   the requirement to be satisfied to activate the card.
     * @param victoryPoints the victory points associated with the card.
     */
    public DepotLeader(int shelfSize, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
        this.shelf = new ResourceShelf(resource, shelfSize);
    }

    @Override
    public ResourceShelf getDepot() { return shelf; } //TODO should be a copy?
}
