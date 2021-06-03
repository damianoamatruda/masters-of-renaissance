package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

import java.util.ArrayList;
import java.util.List;

public class CardRequirements extends StringComponent {
    private final ReducedDevCardRequirement reducedDevCardRequirement;

    public CardRequirements(ReducedDevCardRequirement reducedDevCardRequirement) {
        this.reducedDevCardRequirement = reducedDevCardRequirement;
    }

    @Override
    public String getString(Cli cli) {
        List<String> column = new ArrayList<>();

        column.add("--- Requirements (dev. cards) ---");
        for (int k = 0; k < reducedDevCardRequirement.getEntries().size(); k++) {
            ReducedDevCardRequirementEntry e = reducedDevCardRequirement.getEntries().get(k);
            String req = String.format("  %-49s", e.getAmount() + " " + new Color(e.getColor()).getString(cli) + " card(s) of " +
                    (e.getLevel() > 0 ? ("level " + e.getLevel()) : "any level"));
            column.add(req);
        }

        return String.join("\n", column) + "\n";
    }
}
