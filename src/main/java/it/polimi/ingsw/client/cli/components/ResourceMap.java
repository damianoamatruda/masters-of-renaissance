package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;

import java.util.Map;

public class ResourceMap extends StringComponent {
    private final Map<String, Integer> reducedResourceMap;

    public ResourceMap(Map<String, Integer> reducedResourceMap) {
        this.reducedResourceMap = reducedResourceMap;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        String req = "";
        for (String resType : reducedResourceMap.keySet()) {
            if (i % 2 == 0)
                req = String.format("  %-24s", String.format("%s: %d", new Resource(resType).getString(cli), reducedResourceMap.get(resType)));
            else {
                req += String.format("  %-36s", String.format("%s: %d", new Resource(resType).getString(cli), reducedResourceMap.get(resType)));
                stringBuilder.append(req).append("\n");
            }
            i++;
        }
        if (reducedResourceMap.size() % 2 != 0) {
//            req += " ".repeat(25);
            stringBuilder.append(req).append(" ".repeat(25));

            stringBuilder.append("\n");
        }


        return stringBuilder.toString();
    }
}
