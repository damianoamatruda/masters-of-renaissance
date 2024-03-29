package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Optional;

public class ReducedPlayerSetup {
    /* The number of leader cards to be chosen during the setup. */
    private final int chosenLeadersCount;

    /** The number of choosable resources obtained at the beginning. */
    private final int initialResources;

    /** The resources that cannot be chosen. */
    private final List<String> initialExcludedResources;

    /** The status of finalization of leader cards choice. */
    private final boolean hasChosenLeaders;

    /** The status of finalization of resources choice. */
    private final boolean hasChosenResources;

    /**
     * @param initialResources         the number of choosable resources obtained at the beginning
     * @param initialExcludedResources the resources that cannot be chosen
     * @param hasChosenLeaders         whether all allowed leader cards have been chosen
     * @param hasChosenResources       whether all allowed resources have been chosen
     */
    public ReducedPlayerSetup(int chosenLeadersCount, int initialResources, List<String> initialExcludedResources, boolean hasChosenLeaders, boolean hasChosenResources) {
        this.chosenLeadersCount = chosenLeadersCount;
        this.initialResources = initialResources;
        this.initialExcludedResources = initialExcludedResources;
        this.hasChosenLeaders = hasChosenLeaders;
        this.hasChosenResources = hasChosenResources;
    }

    /**
     * @return the number of leader cards to be chosen during the setup
     */
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
    public Optional<List<String>> getInitialExcludedResources() {
        return Optional.ofNullable(initialExcludedResources);
    }

    /**
     * @return the leader cards have been chosen
     */
    public boolean hasChosenLeaders() {
        return hasChosenLeaders;
    }

    /**
     * @return the resources have been chosen
     */
    public boolean hasChosenResources() {
        return hasChosenResources;
    }
}
