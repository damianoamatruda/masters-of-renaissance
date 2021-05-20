package it.polimi.ingsw.common.reducedmodel;

import java.util.Map;

public class ReducedResourceRequirement {
    private final Map<String, Integer> requirements;

    /**
     * @param requirements
     */
    public ReducedResourceRequirement(Map<String, Integer> requirements) {
        this.requirements = requirements;
    }

    /**
     * @return the requirements of the card
     */
    public Map<String, Integer> getRequirements() {
        return requirements;
    }
}
