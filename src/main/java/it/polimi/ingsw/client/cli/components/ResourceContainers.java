package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.center;

/** Cli component that gives a string representation of multiple resource containers. */
public class ResourceContainers extends StringComponent {
    private final String player;
    private final List<ReducedResourceContainer> warehouseShelves;
    private final List<ReducedResourceContainer> depots;
    private final ReducedResourceContainer strongbox;

    public ResourceContainers(String player, List<ReducedResourceContainer> warehouseShelves,
                              List<ReducedResourceContainer> depots, ReducedResourceContainer strongbox) {
        this.player = player;
        this.warehouseShelves = warehouseShelves;
        this.depots = depots;
        this.strongbox = strongbox;
    }

    @Override
    public String getString() {
        return showShelves() + "\n\n" + showStrongbox();
    }

    private String showShelves() {
        StringBuilder stringBuilder = new StringBuilder();

        if (warehouseShelves.size() > 0) {
            stringBuilder.append(center(String.format("%s's warehouse shelves:", player))).append("\n");
            warehouseShelves.forEach(c ->
                    stringBuilder.append("\n").append(center(new Box(new ResourceContainer(c)).getString())));
        }

        if (depots.size() > 0) {
            stringBuilder.append("\n\n");
            stringBuilder.append(center(String.format("%s's available leader depots:%n", player)));
            depots.forEach(c ->
                    stringBuilder.append("\n").append(center(new Box(new ResourceContainer(c)).getString())));
        }

        return stringBuilder.toString();
    }

    private String showStrongbox() {
        if (strongbox == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(center(String.format("%n%s's strongbox:", player))).append("\n");
        stringBuilder.append("\n").append(center(new Box(new ResourceContainer(strongbox)).getString()));

        return stringBuilder.toString();
    }
}
