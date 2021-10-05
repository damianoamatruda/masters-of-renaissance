package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

/** Cli component that gives a string representation of a resource requirement. */
public class ResourceRequirement extends StringComponent {
    private final ReducedResourceRequirement reducedResourceRequirement;
    private final boolean isCost;

    public ResourceRequirement(ReducedResourceRequirement reducedResourceRequirement, boolean isCost) {
        this.reducedResourceRequirement = reducedResourceRequirement;
        this.isCost = isCost;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("--- %s ---", !isCost ? "Requirements" : "Cost")).append("\n");
        stringBuilder.append(new ResourceMap(reducedResourceRequirement.getRequirements()).getString());
        return stringBuilder.toString();
    }
}
