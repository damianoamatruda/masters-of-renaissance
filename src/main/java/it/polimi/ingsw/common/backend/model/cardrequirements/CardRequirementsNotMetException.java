package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.cardrequirements.DevCardRequirement.Entry;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Exception signaling that the player being examined does not meet the specified requirements.
 *
 * @see CardRequirement
 * @see it.polimi.ingsw.common.backend.model.Player
 */
public class CardRequirementsNotMetException extends Exception {
    private final Set<Entry> missingDevCards;
    private final Map<ResourceType, Integer> missingResources;

    /**
     * Class constructor.
     *
     * @param missingDevCards the entries for the missing development cards in the requirement.
     */
    public CardRequirementsNotMetException(Set<Entry> missingDevCards) {
        this.missingDevCards = missingDevCards;
        this.missingResources = null;
    }

    /**
     * Class constructor.
     *
     * @param missingResources the missing resources with the respective quantities.
     */
    public CardRequirementsNotMetException(Map<ResourceType, Integer> missingResources) {
        this.missingDevCards = null;
        this.missingResources = missingResources;
    }

    /**
     * @return the missingResources
     */
    public Optional<Map<ResourceType, Integer>> getMissingResources() {
        return Optional.ofNullable(missingResources);
    }

    /**
     * @return the missingDevCards
     */
    public Optional<Set<Entry>> getMissingDevCards() {
        return Optional.ofNullable(missingDevCards);
    }
}
