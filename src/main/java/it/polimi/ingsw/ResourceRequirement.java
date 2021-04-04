package it.polimi.ingsw;

import it.polimi.ingsw.leadercards.LeaderCard;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Shelf;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void checkRequirements(Player player) throws Exception {
        List<Shelf> shelves = player.getWarehouse().getShelves().stream().map(e -> (Shelf)e).collect(Collectors.toList());

        for (int i = 0; i < player.getLeadersCount(); i++) {
            if (player.getLeader(i).getDepot() != null)
                shelves.add(player.getLeader(i).getDepot());
        }

        for (ResourceType r : resources.keySet()) {
            int playerAmount = player.getStrongbox().getResourceQuantity(r);
            playerAmount += shelves.stream().filter(e -> e.getResType() == r).mapToInt(s -> s.getResourceQuantity(r)).sum();
            
            if (resources.get(r) - playerAmount > 0) throw new Exception();
        }
    }

    /**
     * Takes the resources that form the requirement from the player. Each storable resource is taken from a given
     * strongbox.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resources are taken from
     * @param strongboxes   a map of the strongboxes where to take the storable resources
     * @throws Exception    if it is not possible
     */
    public void take(Game game, Player player, Map<Strongbox, Map<ResourceType, Integer>> strongboxes) throws Exception {
        (new Production(resources, new HashMap<>(), false))
                .activate(game, player, new HashMap<>(), new HashMap<>(), strongboxes, new HashMap<>());
    }
}
