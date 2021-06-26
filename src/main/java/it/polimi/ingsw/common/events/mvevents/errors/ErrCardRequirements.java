package it.polimi.ingsw.common.events.mvevents.errors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

public class ErrCardRequirements extends ViewEvent {
    private final List<ReducedDevCardRequirementEntry> missingDevCards;
    private final Map<String, Integer> missingResources;

    /**
     * @param view
     * @param missingDevCards  the missing development card entries
     * @param missingResources the missing resources
     */
    public ErrCardRequirements(View view, List<ReducedDevCardRequirementEntry> missingDevCards, Map<String, Integer> missingResources) {
        super(view);
        this.missingDevCards = missingDevCards.isEmpty() ? null : missingDevCards;
        this.missingResources = missingResources.isEmpty() ? null : missingResources;
    }

    /**
     * @return the missingResources
     */
    public Optional<Map<String, Integer>> getMissingResources() {
        return Optional.ofNullable(missingResources);
    }

    /**
     * @return the missingDevCards
     */
    public Optional<List<ReducedDevCardRequirementEntry>> getMissingDevCards() {
        return Optional.ofNullable(missingDevCards);
    }

}
