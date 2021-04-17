package it.polimi.ingsw.model.leadercards;

import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Leader card with the ability to transform white marbles into a resource when taken from the market.
 *
 * @see LeaderCard
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

    /**
     * Complete class constructor needed for automatic construction.
     *
     * @param discount      the amount of resources to be subtracted when applying the ability (unused in this case)
     * @param shelfSize     the maximum amount of resources the card can store (unused in this case)
     * @param production    the production of the card (unused in this case)
     * @param resource      the resource bound to the card. The card's ability is restricted to acting on this resource
     *                      type only.
     * @param requirement   the requirement to be satisfied for card activation
     * @param victoryPoints the victory points associated with the card
     */
    @SuppressWarnings("unused")
    public ZeroLeader(int discount, int shelfSize, Production production, ResourceType resource, CardRequirement requirement, int victoryPoints) {
        this(resource, requirement, victoryPoints);
    }

    @Override
    public Map<ResourceType, Integer> replaceMarketResources(ResourceType replaceableResType,
                                                             Map<ResourceType, Integer> toProcess,
                                                             Map<ResourceType, Integer> replacements) {
        if (toProcess == null) return null;

        if (!isActive()) return super.replaceMarketResources(replaceableResType, toProcess, replacements);

        Map<ResourceType, Integer> resCopy = new HashMap<>(toProcess);
        
        /* If toProcess doesn't have zeros to convert, do nothing;
           if zeros contains this card's resource -> card can be activated (leader was chosen by player) */
        if (toProcess.containsKey(replaceableResType) && replacements.containsKey(this.getResource())) {
            int convertibleAmount = toProcess.get(replaceableResType),
                    chosenAmount = replacements.get(this.getResource()),
                    amountToConvert = Math.min(convertibleAmount, chosenAmount); // can't convert more than the lowest of the two

            /* Add converted resources */
            resCopy.compute(this.getResource(), (res, amount) -> amount == null ? amountToConvert : amount.intValue() + amountToConvert);
            
            /* Remove converted resources, deleting key if none left.
               If there's some left, zeros can be used in successive conversions; else it shouldn't be possible to do so,
               for that would transform more resources than it is allowed */
            resCopy.compute(replaceableResType, (res, amount) -> amount == null || amount.intValue() - amountToConvert == 0 ? null : amount.intValue() - amountToConvert);
            replacements.compute(this.getResource(), (res, amount) -> amount - amountToConvert == 0 ? null : amount - amountToConvert);
        }

        return resCopy;
    }
}
