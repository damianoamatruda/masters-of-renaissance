package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeadersHand implements Renderable {
    private final List<ReducedLeaderCard> leaders;

    public LeadersHand(List<ReducedLeaderCard> leaders) {
        this.leaders = leaders;
    }

    @Override
    public void render(Cli cli) {
        int i;
        for (i = 0; i < leaders.size(); i += 4) {
            cli.getOut().println("─".repeat(40 * Integer.min(4, leaders.size() - i)));
            List<List<String>> rows = new ArrayList<>();

            List<ReducedLeaderCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                cards.add(leaders.get(i + j));
                rows.add(new ArrayList<>());
            }

            for (int j = 0; j < 4 && j < leaders.size() - i; j++)
                rows.get(j).addAll(Arrays.asList(new LeaderCard(cards.get(j)).getString(cli).split("\n")));

            String rowTemplate = "";
            for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                    if (k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.getOut().println("─".repeat(40 * Integer.min(4, leaders.size() - i + 4)));
        cli.getOut().println("\n");
    }
}
