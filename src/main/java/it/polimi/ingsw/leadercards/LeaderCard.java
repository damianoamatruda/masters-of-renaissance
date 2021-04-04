package it.polimi.ingsw.leadercards;

import it.polimi.ingsw.*;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.ResourceShelf;

import java.util.Map;

/**
 * Card with a special ability. Can only be activated or discarded during a game.
 * When active, its ability can be used.
 * Activation requires the player to own either certain resources or development cards.
 */
public class LeaderCard extends Card {
    private final ResourceType resource;
    private final CardRequirement requirement;
    /**
     * The card's status. If active, the ability can be triggered.
     */
    private boolean isActive = false;

    /**
     * Class constructor.
     *
     * @param resource      the resource bound to the card.
     *                      The card's ability is restricted to acting on this resource type only.
     * @param requirement   the requirement to be satisfied for card activation.
     * @param victoryPoints the victory points associated with the card.
     */
    public LeaderCard(ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(victoryPoints);
        assert resource != null && !resource.isBlank();
        this.requirement = requirement;
        this.resource = resource;
    }

    /**
     * Returns the activation status of the card.
     * It doesn't have to be checked prior to the card's use, as it is used internally
     * to check whether the card's ability can be used
     *
     * @return the card's activation status.
     */
    public boolean isActive() { return isActive; }

    /**
     * Activates the card, enabling its effects.
     * 
     * @param player        the player activating the card.
     *                      The card's requirements will be checked on them.
     * @throws Exception    if the player does not meet the card's requirements.
     */
    public void activate(Player player) throws Exception {
        if (requirement != null) requirement.checkRequirements(player);

        isActive = true;
    }

    /**
     * @return  the resource tied to the card. It binds the card's ability to a specific resource type.
     */
    public ResourceType getResource() { return resource; }
    
    /**
     * Executes the discarding routine for leader cards.
     *
     * @param game    the game the player is playing in
     * @param player  the player that discards the card.
     *                All routine effects are applied on this player.
     */
    public void onDiscarded(Game game, Player player) {
        player.incrementFaithPoints(game);
    }

    /**
     * @return  the shelf pertaining to the leader.
     */
    public ResourceShelf getDepot() { return null; }

    /**
     * Applies the leader card's discount.
     *
     * @param devCardCost the cost to apply the discount on.
     * @return  the cost discounted by the card's ability's amount.
     */
    public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> devCardCost) { return devCardCost; }

    /**
     * Returns the <code>Production</code> associated with the leader card.
     *
     * @return the Production object of the leader card.
     */
    public Production getProduction() { return null; }

    /**
     * Processes <code>Zero</code> resources. If the leader is a ZeroLeader,
     * they are replaced by the <code>ResourceType</code> of the leader card.
     *
     * @param toProcess the resources to be processed.
     * @param zeros     the resources to substitute to the zeros.
     *                  If the leader's resource has a non-zero value, the leader is activated, and
     *                  the entry relative to the leader's resource (if present) will be removed.
     *                  This is to ensure proper re-utilization of the current map
     *                  in cascading calls to the method on multiple leaders.
     *                  If there's resources of different type from the leader's, they will be ignored.
     * @return          the resources transformed as per the leader's ability.
     */
    public Map<ResourceType, Integer> processZeros(Map<ResourceType, Integer> toProcess, Map<ResourceType, Integer> zeros) { return toProcess; }
}
