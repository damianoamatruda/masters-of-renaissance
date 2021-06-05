package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

public class DevCardRequirement extends StringComponent {
    private final ReducedDevCardRequirement reducedDevCardRequirement;

    public DevCardRequirement(ReducedDevCardRequirement reducedDevCardRequirement) {
        this.reducedDevCardRequirement = reducedDevCardRequirement;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("--- Requirements ---").append("\n");
        for (int k = 0; k < reducedDevCardRequirement.getEntries().size(); k++) {
            ReducedDevCardRequirementEntry e = reducedDevCardRequirement.getEntries().get(k);
            stringBuilder.append(e.getAmount()).append(" ").append(new DevCardColor(e.getColor()).getString(cli))
                    .append(" card(s) of ").append(e.getLevel() > 0 ? ("level " + e.getLevel()) : "any level").append("\n");
        }

        return stringBuilder.toString();
    }
}
