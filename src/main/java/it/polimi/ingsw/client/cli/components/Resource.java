package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.concurrent.atomic.AtomicReference;

/** Cli component that represents the resources as bold and colored strings. */
public class Resource extends StringComponent {
    private final String resourceType;

    public Resource(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getString() {
        AtomicReference<String> str = new AtomicReference<>();

        Cli.getInstance().getViewModel().getResourceTypes().stream()
                .filter(c -> c.getName().equals(resourceType))
                .map(ReducedResourceType::getAnsiColor)
                .findAny()
                .ifPresentOrElse(
                        color -> str.set(String.format("\u001B[1m%s%s\u001B[0m", color, resourceType)),
                        () -> str.set(resourceType));

        return str.get();
    }
}
