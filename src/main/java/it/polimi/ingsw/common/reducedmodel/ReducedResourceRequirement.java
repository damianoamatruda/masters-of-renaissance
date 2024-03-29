package it.polimi.ingsw.common.reducedmodel;

import java.util.HashMap;
import java.util.Map;

public class ReducedResourceRequirement {
    private final Map<String, Integer> requirements;

    /**
     * @param requirements the map of resource requirement entries
     */
    public ReducedResourceRequirement(Map<String, Integer> requirements) {
        if (requirements == null)
            requirements = new HashMap<>();

        this.requirements = requirements;
    }

    /**
     * @return the requirements of the card
     */
    public Map<String, Integer> getRequirements() {
        return requirements;
    }
}
