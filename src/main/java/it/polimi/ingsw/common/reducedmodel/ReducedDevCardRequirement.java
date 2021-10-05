package it.polimi.ingsw.common.reducedmodel;

import java.util.ArrayList;
import java.util.List;

public class ReducedDevCardRequirement {
    private final List<ReducedDevCardRequirementEntry> entries;

    /**
     * @param entries the development card requirement entries
     */
    public ReducedDevCardRequirement(List<ReducedDevCardRequirementEntry> entries) {
        if (entries == null)
            entries = new ArrayList<>();

        this.entries = entries;
    }

    /**
     * @return the entries
     */
    public List<ReducedDevCardRequirementEntry> getEntries() {
        return entries;
    }
}
