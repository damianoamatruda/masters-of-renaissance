package it.polimi.ingsw.client.cli.components;

import java.util.List;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import static it.polimi.ingsw.client.cli.Cli.center;

public class ResourceContainers extends StringComponent {
    private final String player;
    private final List<ReducedResourceContainer> warehouseShelves;
    private final List<ReducedResourceContainer> depots;
    private final ReducedResourceContainer strongbox;

    public ResourceContainers(String player, List<ReducedResourceContainer> warehouseShelves,
            List<ReducedResourceContainer> depots, ReducedResourceContainer strongbox) {
        
        this.depots = depots;
        this.player = player;
        this.strongbox = strongbox;
        this.warehouseShelves = warehouseShelves;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(showShelves(cli));
        stringBuilder.append(showStrongbox(cli));
        return stringBuilder.toString();
    }

    private String showShelves(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        if (warehouseShelves.size() > 0) {
            stringBuilder.append(String.format("%s's warehouse shelves:", player)).append("\n");
            warehouseShelves.forEach(c ->
                    stringBuilder.append("\n").append(new Box(new ResourceContainer(c)).getString(cli)));
        }
        if (depots.size() > 0) {
            stringBuilder.append("\n");
            stringBuilder.append(String.format("%s's available leader depots:%n", player));
            depots.forEach(c ->
                    stringBuilder.append("\n").append(new Box(new ResourceContainer(c)).getString(cli)));
        }

        return center(stringBuilder.toString());
    }

    private String showStrongbox(Cli cli) {
        if (strongbox == null)
            return "";
        
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%n%s's strongbox:", player)).append("\n");
        stringBuilder.append("\n").append(new Box(new ResourceContainer(strongbox)).getString(cli));

        return center(stringBuilder.toString());
    }
}
