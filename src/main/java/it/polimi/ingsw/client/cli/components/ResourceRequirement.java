package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

public class ResourceRequirement extends StringComponent {
    private final ReducedResourceRequirement reducedResourceRequirement;

    public ResourceRequirement(ReducedResourceRequirement reducedResourceRequirement) {
        this.reducedResourceRequirement = reducedResourceRequirement;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--- Requirements ---").append("\n");
        stringBuilder.append(new ResourceMap(reducedResourceRequirement.getRequirements()).getString(cli));
        return stringBuilder.toString();
    }
}
