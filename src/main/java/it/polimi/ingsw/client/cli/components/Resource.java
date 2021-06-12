package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.concurrent.atomic.AtomicReference;

public class Resource extends StringComponent {
    private final String resourceType;

    public Resource(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getString(Cli cli) {
        AtomicReference<String> str = new AtomicReference<>();

        cli.getViewModel().getResourceTypes().stream()
                .filter(c -> c.getName().equals(resourceType))
                .map(ReducedResourceType::getColorValue)
                .findAny()
                .ifPresentOrElse(
                        color -> str.set(String.format("\u001B[1m%s%s\u001B[0m", color, resourceType)),
                        () -> str.set(resourceType));

        return str.get();
    }
}
