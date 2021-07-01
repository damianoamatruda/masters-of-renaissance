package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

/** Cli component that gives a string representation of a shelf, strongbox or depot. */
public class ResourceContainer extends StringComponent {
    private final ReducedResourceContainer reducedResourceContainer;

    public ResourceContainer(ReducedResourceContainer reducedResourceContainer) {
        this.reducedResourceContainer = reducedResourceContainer;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Container (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---",
                reducedResourceContainer.getId())).append("\n");

        reducedResourceContainer.getBoundedResType().ifPresent(bResType ->
                stringBuilder.append(String.format("Bound resource: %s", new Resource(bResType).getString())).append("\n"));

        if (reducedResourceContainer.getSize() >= 0)
            stringBuilder.append(String.format("Size: %d%n", reducedResourceContainer.getSize()));

        if (reducedResourceContainer.getContent().size() > 0) {
            stringBuilder.append("Content:").append("\n");
            stringBuilder.append(new ResourceMap(reducedResourceContainer.getContent()).getString());
        } else
            stringBuilder.append("(Empty)");

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
