package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

public class ResourceContainer extends StringComponent {
    private final ReducedResourceContainer reducedResourceContainer;
    private int cellWidth = 25;

    public ResourceContainer(ReducedResourceContainer reducedResourceContainer) {
        this.reducedResourceContainer = reducedResourceContainer;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Container (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---",
                reducedResourceContainer.getId())).append("\n");

        reducedResourceContainer.getBoundedResType().ifPresent(bResType ->
                stringBuilder.append(String.format("Bound resource: %s", new Resource(bResType).getString(cli))).append("\n"));

        if (reducedResourceContainer.getSize() >= 0)
            stringBuilder.append(String.format("Size: %d%n", reducedResourceContainer.getSize()));

        if (reducedResourceContainer.getContent().size() > 0) {
            stringBuilder.append("Content:\n");
            stringBuilder.append(new ResourceMap(reducedResourceContainer.getContent()).getString(cli));
        } else
            stringBuilder.append("(Empty)\n");

        return stringBuilder.toString();
    }

    public String getStringBoxed(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("┌").append(Cli.slimLineNoNewLine(cellWidth)).append("┐\n");

        stringBuilder.append("│").append(Cli.left(String.format("--- Container (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---",
                reducedResourceContainer.getId()), cellWidth)).append("│\n");

        reducedResourceContainer.getBoundedResType().ifPresent(bResType ->
                stringBuilder.append("│").append(Cli.left(String.format("Bound resource: %s", new Resource(bResType).getString(cli)), cellWidth)).append("│\n"));

        if (reducedResourceContainer.getSize() >= 0)
            stringBuilder.append("│").append(Cli.left(String.format("Size: %d", reducedResourceContainer.getSize()), cellWidth)).append("│\n");

        if (reducedResourceContainer.getContent().size() > 0) {
            stringBuilder.append("│").append(Cli.left("Content:", cellWidth)).append("│\n");
            stringBuilder.append("│").append(Cli.left(new ResourceMap(reducedResourceContainer.getContent()).getString(cli).replaceAll("\n", ""), cellWidth)).append("│\n");
        } else
            stringBuilder.append("│").append(Cli.left("(Empty)", cellWidth)).append("│\n");

        stringBuilder.append("└").append(Cli.slimLineNoNewLine(cellWidth)).append("┘");

        return stringBuilder.toString();
    }

    public void renderBoxed(Cli cli) {
        cli.getOut().print(getStringBoxed(cli));
    }
}
