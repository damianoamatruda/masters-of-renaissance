package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

import java.util.ArrayList;
import java.util.List;

public class ResourceRequirement extends StringComponent {
    private final ReducedResourceRequirement reducedResourceRequirement;

    public ResourceRequirement(ReducedResourceRequirement reducedResourceRequirement) {
        this.reducedResourceRequirement = reducedResourceRequirement;
    }


    @Override
    public String getString(Cli cli) {
        List<String> column = new ArrayList<>();

        column.add("--- Requirements ---");
        List<String> keys = reducedResourceRequirement.getRequirements().keySet().stream().toList();
        int i = 0;
        String req = "";
        for (String key : keys) {
            if (i % 2 == 0)
                req = String.format("  %-24s", String.format("%s: %d", new Resource(key).getString(cli), reducedResourceRequirement.getRequirements().get(key)));
            else {
                req += String.format("  %-36s", String.format("%s: %d", new Resource(key).getString(cli), reducedResourceRequirement.getRequirements().get(key)));
                column.add(req);
            }
            i++;
        }
        if (keys.size() % 2 != 0) {
            req += " ".repeat(25);
            column.add(req);
        }

        return String.join("\n", column);
    }
}
