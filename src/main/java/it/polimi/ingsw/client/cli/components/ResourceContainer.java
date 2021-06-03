package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

public class ResourceContainer implements Renderable {
    private final ReducedResourceContainer reducedResourceContainer;

    public ResourceContainer(ReducedResourceContainer reducedResourceContainer) {
        this.reducedResourceContainer = reducedResourceContainer;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().printf("Resource Container ID: %d", reducedResourceContainer.getId());
        if (reducedResourceContainer.getboundedResType() != null)
            cli.getOut().printf(", bounded resource: %s", new Resource(reducedResourceContainer.getboundedResType()).getString(cli));
        cli.getOut().printf(", size: %d%n", reducedResourceContainer.getSize());

        reducedResourceContainer.getContent().forEach((key, value) -> cli.getOut().println(new Resource(key).getString(cli) + ": " + value));
        cli.getOut().println();
    }
}
