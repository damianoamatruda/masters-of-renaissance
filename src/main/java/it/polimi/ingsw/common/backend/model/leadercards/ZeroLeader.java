package it.polimi.ingsw.common.backend.model.leadercards;

import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard.LeaderType;

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
     * @param id            the card identifier number
     */
    public ZeroLeader(ResourceType resource, CardRequirement requirement, int victoryPoints, int id) {
        super(resource, requirement, victoryPoints, id);
    }

    @Override
    public Map<ResourceType, Integer> replaceMarketResources(
            ResourceType replaceableResType,
            Map<ResourceType, Integer> toProcess,
            Map<ResourceType, Integer> replacements) {

        if (toProcess == null)
            return null;

        if (!isActive())
            return super.replaceMarketResources(replaceableResType, toProcess, replacements);

        Map<ResourceType, Integer> resCopy = new HashMap<>(toProcess);
        
        /* If toProcess does not have zeros to convert, do nothing;
           if zeros contains this card's resource -> card can be activated (leader was chosen by player) */
        if (toProcess.containsKey(replaceableResType) && replacements.containsKey(this.getResource())) {
            int convertibleQuantity = toProcess.get(replaceableResType),
                    chosenQuantity = replacements.get(this.getResource()),
                    quantityToConvert = Math.min(convertibleQuantity, chosenQuantity); // Cannot convert more than the lowest of the two

            /* Add converted resources */
            resCopy.compute(this.getResource(), (res, quantity) -> quantity == null ? quantityToConvert : quantity + quantityToConvert);
            
            /* Remove converted resources, deleting key if none left.
               If there is some left, zeros can be used in successive conversions; else it should not be possible to do so,
               for that would transform more resources than it is allowed */
            resCopy.compute(replaceableResType, (res, quantity) -> quantity == null || quantity - quantityToConvert == 0 ? null : quantity - quantityToConvert);
            replacements.compute(this.getResource(), (res, quantity) -> quantity == null || quantity - quantityToConvert == 0 ? null : quantity - quantityToConvert);
        }

        return resCopy;
    }

    @Override
    public ReducedLeaderCard reduce() {
        return new ReducedLeaderCard(getId(), getVictoryPoints(), getResource().getName(), LeaderType.ZERO,
                isActive(), requirement.reduceDR(), requirement.reduceRR(), -1, 0, -1);
    }
}
