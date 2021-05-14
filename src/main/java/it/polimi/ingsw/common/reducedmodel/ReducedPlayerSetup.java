package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedPlayerSetup {
    private final int chosenLeadersCount;

    /** The number of choosable resources obtained at the beginning. */
    private final int initialResources;

    /** The resources that cannot be chosen. */
    private final List<String> initialExcludedResources;

    private final boolean hasChosenLeaders;

    private final boolean hasChosenResources;

    /**
     * @param initialResources         the number of choosable resources obtained at the beginning
     * @param initialExcludedResources the resources that cannot be chosen
     * @param hasChosenLeaders
     * @param hasChosenResources
     */
    public ReducedPlayerSetup(int chosenLeadersCount, int initialResources, List<String> initialExcludedResources, boolean hasChosenLeaders, boolean hasChosenResources) {
        this.chosenLeadersCount = chosenLeadersCount;
        this.initialResources = initialResources;
        this.initialExcludedResources = initialExcludedResources;
        this.hasChosenLeaders = hasChosenLeaders;
        this.hasChosenResources = hasChosenResources;
    }

    public int getChosenLeadersCount() {
        return chosenLeadersCount;
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

    public boolean hasChosenLeaders() {
        return hasChosenLeaders;
    }

    public boolean hasChosenResources() {
        return hasChosenResources;
    }
}
