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
            stringBuilder.append(String.format(
                    "  %s %s × %d", new DevCardColor(e.getColor()).getString(),
                    e.getLevel() == 0 ? "any level" : String.format("level %d", e.getLevel()),
                    e.getAmount())).append("\n");
        }

        return stringBuilder.toString();
    }
}
