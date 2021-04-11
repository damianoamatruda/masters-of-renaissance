package it.polimi.ingsw.model.cardrequirements;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcecontainers.Shelf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResourceRequirement(Map<ResourceType, Integer> resources) { this.resources = resources; }

    /**
     * Returns the cost of this requirement discounted by the player's leaders
     * 
     * @param p the player whose leader are to be used in the discounting process.
     * @return  the discounted cost of this requirement.
     */
    private Map<ResourceType, Integer> getDiscountedCost(Player p) {
        Map<ResourceType, Integer> discountedRes = new HashMap<>(resources);

        for (LeaderCard l : p.getLeaders())
            discountedRes = l.getDevCardCost(discountedRes);

        return discountedRes;
    }

    @Override
    public void checkRequirements(Player player) throws Exception {
        Map<ResourceType, Integer> discountedRes = getDiscountedCost(player);

        List<Shelf> shelves = player.getWarehouse().getShelves().stream().map(e -> (Shelf)e).collect(Collectors.toList());

        for (int i = 0; i < player.getLeaders().size(); i++) {
            if (player.getLeaders().get(i).getDepot() != null)
                shelves.add(player.getLeaders().get(i).getDepot());
        }

        for (ResourceType r : discountedRes.keySet()) {
            int playerAmount = player.getStrongbox().getResourceQuantity(r);
            playerAmount += shelves.stream().filter(e -> e.getResType() == r).mapToInt(s -> s.getResourceQuantity(r)).sum();
            
            if (discountedRes.get(r) - playerAmount > 0) throw new Exception();
        }
    }

    /**
     * Takes the resources that form the requirement from the player. Each storable resource is taken from a given
     * resource container.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resources are taken from
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws Exception    if it is not possible
     */
    public void take(Game game, Player player, Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws Exception {
        new Production(getDiscountedCost(player), 0, new HashMap<>(), 0)
                .activate(game, player, new HashMap<>(), new HashMap<>(), resContainers, new HashMap<>());
    }
}