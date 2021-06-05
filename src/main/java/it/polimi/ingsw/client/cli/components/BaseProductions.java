package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseProductions extends StringComponent {
    private final Map<String, Integer> baseProductions;

    public BaseProductions(Map<String, Integer> baseProductions) {
        this.baseProductions = baseProductions;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder output = new StringBuilder("Base productions:").append("\n");

        baseProductions.forEach((key, value) -> output.append("Player: ").append(key).append("\n"));
        List<Integer> baseProds = baseProductions.values().stream().toList();
        for (int i = 0; i < baseProds.size(); i += 3) {
            List<List<String>> rows = new ArrayList<>();

            for (int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                rows.add(new ArrayList<>());
                List<String> column = rows.get(j);
                cli.getViewModel().getProduction(baseProds.get(3 * i + j)).ifPresent(p ->
                        column.addAll(Arrays.asList(new ResourceTransactionRecipe(p).getString(cli).split("\\R"))));
            }

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                for (int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                    if (k < rows.get(j).size())
                        output.append(Cli.left(rows.get(j).get(k), 50));
                }
                output.append("\n");
            }
            output.append("\n\n");
        }
        return output.toString();
    }
}
