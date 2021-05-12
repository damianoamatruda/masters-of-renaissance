package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedBoost {
    /** The number of choosable resources obtained at the beginning. */
    private final int initialResources;

    /** The resources that cannot be chosen. */
    private final List<String> initialExcludedResources;

    /**
     * @param initialResources         the number of choosable resources obtained at the beginning
     * @param initialExcludedResources the resources that cannot be chosen
     */
    public ReducedBoost(int initialResources, List<String> initialExcludedResources) {
        this.initialResources = initialResources;
        this.initialExcludedResources = initialExcludedResources;
    }

    /**
     * @return the number of choosable resources obtained at the beginning
     */
    public int getInitialResources() {
        return initialResources;
    }

    /**
     * @return the resources that cannot be chosen
     */
    public List<String> getInitialExcludedResources() {
        return initialExcludedResources;
    }
}
