package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A concrete requirement for leader card activation.
 * <p>
 * A <code>LeaderCard</code> with a <code>ResourceRequirement</code> can only be activated if the <code>Player</code>
 * that owns it also owns the specified quantity of <code>Resource</code>s.
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
        this.resources = new HashMap<>(resources);
    }

    /**
     * Returns the cost of this requirement discounted by the player's leaders
     *
     * @param p the player whose leader are to be used in the discounting process.
     * @return the discounted cost of this requirement.
     */
    public Map<ResourceType, Integer> getDiscountedCost(Player p) {
        Map<ResourceType, Integer> discountedRes = new HashMap<>(resources.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

        for (LeaderCard l : p.getLeaders())
            discountedRes = l.getDevCardCost(discountedRes);

        return discountedRes;
    }

    @Override
    public void checkRequirements(Player player) throws CardRequirementsNotMetException {
        /* in order for the player to be able to buy the devcard,
         * satisfying the discounted cost is enough
         * for each resource type in the requirement,
         * the player is then checked for ownership of the specified quantity */

        Map<ResourceType, Integer> discountedRes = getDiscountedCost(player),
                missingResources = new HashMap<>();

        // get all resource containers owned by the player
        Set<ResourceContainer> resContainers = player.getResContainers();

        for (ResourceType r : discountedRes.keySet()) {
            // get the quantity of this resource the player owns
            int playerQuantity = resContainers.stream().mapToInt(c -> c.getResourceQuantity(r)).sum();

            // if the player does not own enough of this resource, the requirements are not met
            if (discountedRes.get(r) - playerQuantity > 0)
                missingResources.put(r, discountedRes.get(r) - playerQuantity);
        }

        if (!missingResources.isEmpty())
            throw new CardRequirementsNotMetException(missingResources);
    }

    @Override
    public ReducedResourceRequirement reduceRR() {
        return new ReducedResourceRequirement(
                resources.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)));
    }

    @Override
    public ReducedDevCardRequirement reduceDR() {
        return null;
    }
}
