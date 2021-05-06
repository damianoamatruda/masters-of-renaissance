package it.polimi.ingsw.server.model.cardrequirements;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A concrete requirement for leader card activation.
 * <p>
 * A <code>LeaderCard</code> with a <code>ResourceRequirement</code> can only be activated if the <code>Player</code>
 * that owns it also owns the specified amount of <code>Resource</code>s.
 *
 * @see LeaderCard
 * @see CardRequirement
 * @see Player
 */
public class ResourceRequirement implements CardRequirement {
    /** The resources required to activate the leader card. */
    private final Map<ResourceType, Integer> resources;

    /**
     * Class constructor.
     *
     * @param resources the resources that form the requirement
     */
    public ResourceRequirement(Map<ResourceType, Integer> resources) {
        this.resources = resources;
    }

    /**
     * Returns the cost of this requirement discounted by the player's leaders
     *
     * @param p the player whose leader are to be used in the discounting process.
     * @return the discounted cost of this requirement.
     */
    private Map<ResourceType, Integer> getDiscountedCost(Player p) {
        Map<ResourceType, Integer> discountedRes = new HashMap<>(resources);

        for (LeaderCard l : p.getLeaders())
            discountedRes = l.getDevCardCost(discountedRes);

        return discountedRes;
    }

    @Override
    public void checkRequirements(Player player) throws CardRequirementsNotMetException {
        /* in order for the player to be able to buy the devcard,
         * satisfying the discounted cost is enough
         * for each resource type in the requirement,
         * the player is then checked for ownership of the specified amount */

        Map<ResourceType, Integer> discountedRes = getDiscountedCost(player),
                missingResources = new HashMap<>();

        // get all warehouse shelves
        List<Shelf> shelves = new ArrayList<>(List.copyOf(player.getWarehouse().getShelves()));

        // add the leaders' depots (they count as warehouse shelves)
        for (int i = 0; i < player.getLeaders().size(); i++)
            player.getLeaders().get(i).getDepot().ifPresent(shelves::add);

        for (ResourceType r : discountedRes.keySet()) {
            // get the amount of this resource the player owns from both the strongbox
            int playerAmount = player.getStrongbox().getResourceQuantity(r);
            // and every shelf the resource of which matches the currently examined resource
            playerAmount += shelves.stream().filter(e -> e.getResourceType().isPresent() && e.getResourceType().get().equals(r)).mapToInt(s -> s.getResourceQuantity(r)).sum();

            // if the player does not own enough of this resource, the requirements aren't met
            if (discountedRes.get(r) - playerAmount > 0)
                missingResources.put(r, discountedRes.get(r) - playerAmount);
        }

        if (!missingResources.keySet().isEmpty()) {
            String msg = String.format("\nPlayer %s lacks the following resources by the following amounts:", player.getNickname());

            for (Map.Entry<ResourceType, Integer> e : missingResources.entrySet()) {
                msg = msg.concat(String.format("\nResource %s, missing %s", e.getKey().getName(), e.getValue()));
            }

            throw new CardRequirementsNotMetException("resource", msg);
        }
    }

    /**
     * Takes the resources that form the requirement from the player. Each storable resource is taken from a given
     * resource container.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resources are taken from
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws CardRequirementsNotMetException if it is not possible
     */
    public void take(Game game, Player player, Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws CardRequirementsNotMetException {
        try {
            new ProductionGroup(List.of(
                    new ProductionGroup.ProductionRequest(
                            new Production(getDiscountedCost(player), 0, Map.of(), 0),
                            Map.of(), Map.of(), Map.of(), resContainers, Map.of())
            )).activate(game, player);
        } catch (IllegalProductionActivationException e) {
            throw new CardRequirementsNotMetException("resource", e);
        }
    }
}
