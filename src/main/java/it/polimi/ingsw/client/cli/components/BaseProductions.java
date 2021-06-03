package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseProductions implements Renderable {
    private final Map<String, Integer> baseProductions;

    public BaseProductions(Map<String, Integer> baseProductions) {
        this.baseProductions = baseProductions;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println("Base productions:");

        baseProductions.forEach((key, value) -> cli.getOut().println("Player: " + key));
        List<Integer> baseProds = baseProductions.values().stream().toList();
        for (int i = 0; i < baseProds.size(); i += 3) {
            List<List<String>> rows = new ArrayList<>();

            String rowTemplate = "";
            for (int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                rows.add(new ArrayList<>());
                List<String> column = rows.get(j);
                cli.getViewModel().getProduction(baseProds.get(3 * i + j)).ifPresent(p ->
                        column.addAll(Arrays.asList(new ResourceTransactionRecipe(p).getString(cli).split("\\R"))));
                rowTemplate += "%-50s ";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                    if (k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
            cli.getOut().println("\n");
        }
    }
}
