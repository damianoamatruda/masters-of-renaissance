package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

/** Cli component that gives a string representation of a resource requirement. */
public class ResourceRequirement extends StringComponent {
    private final ReducedResourceRequirement reducedResourceRequirement;

    public ResourceRequirement(ReducedResourceRequirement reducedResourceRequirement) {
        this.reducedResourceRequirement = reducedResourceRequirement;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--- Requirements ---").append("\n");
        stringBuilder.append(new ResourceMap(reducedResourceRequirement.getRequirements()).getString());
        return stringBuilder.toString();
    }
}
