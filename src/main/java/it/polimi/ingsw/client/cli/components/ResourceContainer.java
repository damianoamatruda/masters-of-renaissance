package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

public class ResourceContainer extends StringComponent {
    private final ReducedResourceContainer reducedResourceContainer;

    public ResourceContainer(ReducedResourceContainer reducedResourceContainer) {
        this.reducedResourceContainer = reducedResourceContainer;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Resource Container (ID: %d) ---%n", reducedResourceContainer.getId()));

        if (reducedResourceContainer.getboundedResType() != null)
            stringBuilder.append(String.format("Bounded resource: %s%n", new Resource(reducedResourceContainer.getboundedResType()).getString(cli)));

        if (reducedResourceContainer.getSize() >= 0)
            stringBuilder.append(String.format("Size: %d%n", reducedResourceContainer.getSize()));

        if (reducedResourceContainer.getContent().size() > 0) {
            stringBuilder.append("Content:\n");
            stringBuilder.append(new ResourceMap(reducedResourceContainer.getContent()).getString(cli));
        } else
            stringBuilder.append("(Empty)\n");

        return stringBuilder.toString();
    }
}
