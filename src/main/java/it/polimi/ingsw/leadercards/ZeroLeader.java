package it.polimi.ingsw.leadercards;

import it.polimi.ingsw.CardRequirement;
import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.resourcetypes.Zero;

import java.util.Map;

/**
 * Leader card with the ability to transform white marbles into a resource
 * when taken from the market.
 */
public class ZeroLeader extends LeaderCard {
    /**
     * Class constructor.
     *
     * @param resource      the resource bound to the card.
     *                      The card's ability is restricted to acting on this resource type only.
     * @param requirement   the requirement to be satisfied for card activation.
     * @param victoryPoints the victory points associated with the card.
     */
    public ZeroLeader(ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
    }

    @Override
    public Map<ResourceType, Integer> processZeros(Map<ResourceType, Integer> resources, Map<ResourceType, Integer> zeros) {
        Map<ResourceType, Integer> resCopy = Map.copyOf(resources);
        
        // if zeros contains this card's resource -> card has to be activated (leader was chosen by player)
        if (zeros.containsKey(this.getResource())) {
            // take amount of resources of this type to be substituted from zeros map
            // and add them to the output resources
            resCopy.compute(this.getResource(), (res, amount) -> amount + zeros.get(res));
            
            // subtract amount of substituted resources from zero key
            resCopy.computeIfPresent(Zero.getInstance(), (res, amount) -> amount - zeros.get(res));
            
            // delete entry so it can't be re-processed when called in cascade with other leaders
            zeros.remove(this.getResource());
        }
        
        return resCopy;
    }
}
