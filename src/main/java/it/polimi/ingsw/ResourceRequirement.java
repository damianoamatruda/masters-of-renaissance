package it.polimi.ingsw;

import it.polimi.ingsw.leadercards.LeaderCard;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.HashMap;
import java.util.Map;

/**
 * A concrete requirement for leader card activation.
 *
 * A <code>LeaderCard</code> with a <code>ResourceRequirement</code> can only be activated
 * if the <code>Player</code> that owns it also owns the specified amount of <code>Resource</code>s.
 *
 * @see LeaderCard
 * @see CardRequirement
 * @see Player
 */
public class ResourceRequirement implements CardRequirement {
    /**
     * The resources required to activate the leader card.
     */
    private final Map<ResourceType, Integer> resources;

    /**
     * Class constructor.
     *
     * @param resources the resources that form the requirement.
     */
    public ResourceRequirement(Map<ResourceType, Integer> resources) { this.resources = resources; }

    @Override
    public void checkRequirements(Player player) {
        // TODO: Implement
    }

    /**
     * Takes the resources that form the requirement from the player. Each storable resource is taken from a given
     * strongbox.
     *
     * @param player        the player the resources are taken from
     * @param strongboxes   a map of the strongboxes where to take the storable resources
     * @throws Exception    if it is not possible
     */
    public void take(Player player, Map<Strongbox, Map<ResourceType, Integer>> strongboxes) throws Exception {
        (new Production(resources, new HashMap<>(), false))
                .activate(player, new HashMap<>(), new HashMap<>(), strongboxes, new HashMap<>());
    }
}
