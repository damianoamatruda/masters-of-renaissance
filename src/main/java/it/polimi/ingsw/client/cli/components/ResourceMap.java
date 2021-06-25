package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;

import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.left;

public class ResourceMap extends StringComponent {
    private final int padding = 12;
    private final Map<String, Integer> reducedResourceMap;

    public ResourceMap(Map<String, Integer> reducedResourceMap) {
        this.reducedResourceMap = reducedResourceMap;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for (String resType : reducedResourceMap.keySet()) {
            if (i % 2 == 0)
                stringBuilder.append(String.format("  %s", left(String.format("%s: %d", new Resource(resType).getString(cli), reducedResourceMap.get(resType)), padding)));
            else
                stringBuilder.append(String.format("%s: %d", new Resource(resType).getString(cli), reducedResourceMap.get(resType))).append("\n");
            i++;
        }
        if (reducedResourceMap.size() % 2 != 0)
            stringBuilder.append("\n");

        return stringBuilder.toString();
    }

}
