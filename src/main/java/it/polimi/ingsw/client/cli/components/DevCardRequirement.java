package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

/** Cli component that gives a string representation of development cards requirements. */
public class DevCardRequirement extends StringComponent {
    private final ReducedDevCardRequirement reducedDevCardRequirement;

    public DevCardRequirement(ReducedDevCardRequirement reducedDevCardRequirement) {
        this.reducedDevCardRequirement = reducedDevCardRequirement;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("--- Requirements ---").append("\n");
        for (int k = 0; k < reducedDevCardRequirement.getEntries().size(); k++) {
            ReducedDevCardRequirementEntry e = reducedDevCardRequirement.getEntries().get(k);
            stringBuilder.append(e.getAmount()).append(" ").append(new DevCardColor(e.getColor()).getString())
                    .append(" card(s) of ").append(e.getLevel() > 0 ? ("level " + e.getLevel()) : "any level").append("\n");
        }

        return stringBuilder.toString();
    }
}
