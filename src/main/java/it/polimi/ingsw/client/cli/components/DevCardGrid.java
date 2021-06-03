package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

import java.util.*;

public class DevCardGrid implements Renderable {
    private final ReducedDevCardGrid reducedDevCardGrid;

    public DevCardGrid(ReducedDevCardGrid reducedDevCardGrid) {
        this.reducedDevCardGrid = reducedDevCardGrid;
    }

    @Override
    public void render(Cli cli) {
        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int levels = reducedDevCardGrid.getLevelsCount();

        for (int i = 1; i <= levels; i++) {
            cli.trackSlimLine();
            List<List<String>> lines = new ArrayList<>();
            for (String key : reducedDevCardGrid.getGrid().keySet()) {
                int index = i;
                ReducedDevCard card = reducedDevCardGrid.getGrid().get(key).stream()
                        .filter(Objects::nonNull).map(Stack::peek)
                        .map(id -> cli.getViewModel().getDevelopmentCard(id).orElse(null))
                        .filter(Objects::nonNull)
                        .filter(c -> c.getLevel() == levels + 1 - index).findAny().orElseThrow();
                topCards.add(new ArrayList<>());
                topCards.get(i - 1).add(card);
            }
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                ReducedDevCard card = topCards.get(i - 1).get(j);
                lines.add(new ArrayList<>());
                List<String> column = lines.get(j);
                column.addAll(Arrays.asList(new DevelopmentCard(card).getString(cli).split("\\R")));
            }

            String rowTemplate = "";
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = lines.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int j = 0; j < length; j++) {
                List<String> row = new ArrayList<>();
                for (int l = 0; l < topCards.get(i - 1).size(); l++) {
                    if (j < lines.get(l).size())
                        row.add(lines.get(l).get(j));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.trackSlimLine();
        cli.getOut().println();
    }
}
