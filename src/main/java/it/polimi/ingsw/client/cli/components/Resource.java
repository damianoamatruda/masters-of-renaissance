package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

public class Resource extends StringComponent {
    private final String resourceType;

    public Resource(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getString(Cli cli) {
        String color = cli.getViewModel().getResourceTypes().stream().filter(c -> c.getName().equals(resourceType)).map(ReducedResourceType::getColorValue).findAny().orElseThrow();
        return String.format("\u001B[1m%s%s\u001B[0m", color, resourceType);
    }
}
