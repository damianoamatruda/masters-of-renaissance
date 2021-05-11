package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedDevCardRequirement extends ReducedCardRequirement {
    private final List<ReducedDevCardRequirementEntry> entries;

    /**
     * @param entries
     */
    public ReducedDevCardRequirement(List<ReducedDevCardRequirementEntry> entries) {
        this.entries = entries;
    }

    /**
     * @return the entries
     */
    public List<ReducedDevCardRequirementEntry> getEntries() {
        return entries;
    }
}
