package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.Card;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceShelf;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateLeader;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.Map;
import java.util.Optional;

/**
 * Card with a special ability. Can only be activated or discarded during a game. When active, its ability can be used.
 * Activation requires the player to own either certain resources or development cards.
 *
 * @see Card
 * @see CardRequirement
 * @see Player
 */
public abstract class LeaderCard extends Card {
    private final ResourceType resource;
    protected final CardRequirement requirement; // TODO: Merge ReducedDepotLeaderCard, ReducedDiscountLeaderCard, ReducedProductionLeaderCard and ReducedLeaderCard

    /** The card's status. If active, the ability can be triggered. */
    private boolean active = false;

    /**
     * Class constructor.
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied for card activation.
     * @param victoryPoints the victory points associated with the card.
     * @param id            the card identifier number
     */
    public LeaderCard(ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(victoryPoints, id);
        this.resource = resource;
        this.requirement = requirement;
    }

    /**
     * Returns the activation status of the card. It doesn't have to be checked prior to the card's use, since the
     * card's status is already checked for internally
     *
     * @return the card's activation status.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activates the card, enabling its effects.
     *
     * @param player the player activating the card. The card's requirements will be checked on them.
     * @throws CardRequirementsNotMetException if the leader's requirements are not met when activating
     * @throws IllegalArgumentException        player does not own this card
     */
    public void activate(Player player) throws IllegalArgumentException, CardRequirementsNotMetException {
        if (!player.getLeaders().contains(this))
            throw new IllegalArgumentException(
                    String.format("Illegal leader choice: leader isn't owned by player %s", player.getNickname()));
        if (requirement != null)
            requirement.checkRequirements(player);

        active = true;

        player.incrementVictoryPoints(getVictoryPoints());
        dispatch(new UpdateLeader(getId(), active));
    }

    /**
     * @return the resource tied to the card. It binds the card's ability to a specific resource type.
     */
    public ResourceType getResource() {
        return resource;
    }

    /**
     * @return the shelf pertaining to the leader.
     */
    public Optional<ResourceShelf> getDepot(boolean isFactoryAsking) {
        return Optional.empty();
    }

    /**
     * Applies the leader card's discount.
     *
     * @param devCardCost the cost to apply the discount on.
     * @return the cost discounted by the card's ability's amount.
     */
    public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> devCardCost) {
        return devCardCost;
    }

    /**
     * @return the Production object of the leader card.
     */
    public Optional<ResourceTransactionRecipe> getProduction(boolean isFactoryAsking) {
        return Optional.empty();
    }

    /**
     * Processes <code>Zero</code> resources. If the leader is a ZeroLeader, they are replaced by the
     * <code>ResourceType</code> of the leader card.
     *
     * @param replaceableResType the type of the resources to be replaced
     * @param toProcess          the resources to be processed
     * @param replacements       the resources to substitute to the replaceable resources. If the leader's resource has
     *                           a non-zero value, the leader is activated, and the entry relative to the leader's
     *                           resource (if present) will be removed. This is to ensure proper re-utilization of the
     *                           current map in cascading calls to the method on multiple leaders. If there's resources
     *                           of different type from the leader's, they will be ignored.
     * @return the resources transformed as per the leader's ability
     */
    public Map<ResourceType, Integer> replaceMarketResources(ResourceType replaceableResType,
                                                             Map<ResourceType, Integer> toProcess,
                                                             Map<ResourceType, Integer> replacements) {
        return toProcess;
    }

    public abstract ReducedLeaderCard reduce();
}
