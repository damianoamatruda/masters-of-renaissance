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

            for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                List<String> column = rows.get(j);
                ReducedLeaderCard reducedLeaderCard = cards.get(j);

                column.add(String.format("%-38s", "[Leader]"));

                column.add(String.format("%-51s", String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, type: %s",
                        reducedLeaderCard.getId(),
                        reducedLeaderCard.getLeaderType()
                )));

                column.add(String.format("%-51s", String.format("BoundResource: %s, VP: %d",
                        new Resource(reducedLeaderCard.getResourceType().getName()).getString(cli),
                        reducedLeaderCard.getVictoryPoints())));

                column.add(String.format("%-38s", String.format("Active status: %s", reducedLeaderCard.isActive())));

                if (reducedLeaderCard.getContainerId() > -1)
                    column.add(String.format("%-38s", String.format("Depot ID: %d", reducedLeaderCard.getContainerId())));
                if (reducedLeaderCard.getProduction() > -1)
                    column.add(String.format("%-38s", String.format("Production ID: %d", reducedLeaderCard.getProduction())));
                if (reducedLeaderCard.getDiscount() > -1)
                    column.add(String.format("%-38s", String.format("Discount: %d", reducedLeaderCard.getDiscount())));

                if (reducedLeaderCard.getDevCardRequirement() != null)
                    column.addAll(Arrays.asList(new CardRequirements(reducedLeaderCard.getDevCardRequirement()).getString(cli).split("\n")));
                if (reducedLeaderCard.getResourceRequirement() != null)
                    column.addAll(Arrays.asList(new ResRequirements(reducedLeaderCard.getResourceRequirement()).getString(cli).split("\n")));
                cli.getViewModel().getProduction(reducedLeaderCard.getProduction()).ifPresent(p ->
                        column.addAll(Arrays.asList(new ResourceTransactionRecipe(p).getString(cli).split("\n"))));
            }

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
