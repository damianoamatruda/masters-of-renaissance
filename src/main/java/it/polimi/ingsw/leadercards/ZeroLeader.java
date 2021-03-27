package it.polimi.ingsw.leadercards;

import it.polimi.ingsw.CardRequirement;
import it.polimi.ingsw.resourcetypes.ResourceType;

import java.util.Map;

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

    //TODO implement
    @Override
    public Map<ResourceType, Integer> processZeros(Map<ResourceType, Integer> resources, Map<ResourceType, Integer> zeros) { return resources; }
}
