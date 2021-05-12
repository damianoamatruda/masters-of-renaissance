package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedBoost {
    /** Number of choosable resources obtained at the beginning. */
    private final int initialResources;
    /** Resources that cannot be chosen. */
    private final List<String> initialExcludedResources;
    
    /**
     * @param initialResources
     * @param initialFaith
     * @param initialExcludedResources
     */
    public ReducedBoost(int initialResources, List<String> initialExcludedResources) {
        this.initialResources = initialResources;
        this.initialExcludedResources = initialExcludedResources;
    }

    /**
     * @return the initialResources
     */
    public int getInitialResources() {
        return initialResources;
    }

    /**
     * @return the initialExcludedResources
     */
    public List<String> getInitialExcludedResources() {
        return initialExcludedResources;
    }
}
