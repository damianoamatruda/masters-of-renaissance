package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.CardRequirement;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcetypes.Zero;

import java.util.HashMap;
import java.util.Map;

/**
 * Leader card with the ability to transform white marbles into a resource when taken from the market.
 */
public class ZeroLeader extends LeaderCard {
    /**
     * Class constructor.
     *
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied for card activation
     * @param victoryPoints the victory points associated with the card
     */
    public ZeroLeader(ResourceType resource, CardRequirement requirement, int victoryPoints) {
        super(resource, requirement, victoryPoints);
    }

    @Override
    public Map<ResourceType, Integer> processZeros(Map<ResourceType, Integer> toProcess, Map<ResourceType, Integer> zeros) {
        if (toProcess == null) return null;

        if (!isActive()) return super.processZeros(toProcess, zeros);
        
        Map<ResourceType, Integer> resCopy = new HashMap<>(toProcess);
        
        /* If toProcess doesn't have zeros to convert, do nothing;
           if zeros contains this card's resource -> card can be activated (leader was chosen by player) */
        if (toProcess.containsKey(Zero.getInstance()) && zeros.containsKey(this.getResource())) {
            int convertibleAmount = toProcess.get(Zero.getInstance()),
                chosenAmount = zeros.get(this.getResource()),
                amountToConvert = Math.min(convertibleAmount, chosenAmount); // can't convert more than the lowest of the two

            /* Add converted resources */
            resCopy.compute(this.getResource(), (res, amount) -> amount == null ? amountToConvert : amount + amountToConvert);
            
            /* Remove converted resources, deleting key if none left.
               If there's some left, zeros can be used in successive conversions; else it shouldn't be possible to do so,
               for that would transform more resources than it is allowed */
            resCopy.compute(Zero.getInstance(), (res, amount) -> amount - amountToConvert == 0 ? null : amount - amountToConvert);
            zeros.compute(this.getResource(), (res, amount) -> amount - amountToConvert == 0 ? null : amount - amountToConvert);
        }
        
        return resCopy;
    }
}
