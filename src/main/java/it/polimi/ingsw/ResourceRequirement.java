package it.polimi.ingsw;

import it.polimi.ingsw.leadercards.LeaderCard;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Strongbox;

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

    /**
     * Checks whether the player owns the resources necessary to activate the leader card.
     *
     * @param player  the player to be checked for resource ownership.
     */
    @Override
    public void checkRequirements(Player player) {
        // TODO Auto-generated method stub

    }

    /**
     * Gives the resources that form the requirement. Each resource is taken from a given strongbox.
     *
     * @param strongboxes   a map of the strongboxes where to take the given resources, if possible
     */
    public void giveResources(Map<ResourceType, Map<Strongbox, Integer>> strongboxes) {
        // TODO Implement
    }
}
