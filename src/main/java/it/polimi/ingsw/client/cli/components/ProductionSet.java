package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.maxLinesHeight;

/** Cli component that gives a string representation of multiple productions. */
public class ProductionSet extends StringComponent {
    private final List<ReducedResourceTransactionRecipe> reducedProductionSet;

    public ProductionSet(List<ReducedResourceTransactionRecipe> reducedProductionSet) {
        this.reducedProductionSet = reducedProductionSet;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < reducedProductionSet.size(); i += 4) {
            List<ReducedResourceTransactionRecipe> productions = new ArrayList<>();
            for (int j = 0; j < 4 && j < reducedProductionSet.size() - i; j++) {
                productions.add(reducedProductionSet.get(i + j));
            }

            List<ResourceTransactionRecipe> cliProductions = new ArrayList<>();
            for (int j = 0; j < 4 && j < reducedProductionSet.size() - i; j++)
                cliProductions.add(new ResourceTransactionRecipe(productions.get(j)));

            int maxWidth = cliProductions.stream().map(ResourceTransactionRecipe::getString).mapToInt(Cli::maxLineWidth).max().orElse(0);
            int maxHeight = maxLinesHeight(cliProductions.stream().map(ResourceTransactionRecipe::getString).toList());

            List<List<String>> rows = new ArrayList<>();
            for (ResourceTransactionRecipe prod : cliProductions)
                rows.add(new Box(prod, -1, maxWidth, maxHeight).getString().lines().toList());

            int length = rows.stream().mapToInt(List::size).max().orElse(0);
            for (int k = 0; k < length; k++) {
                for (List<String> row : rows) {
                    if (k < row.size())
                        stringBuilder.append(row.get(k)).append(" ");
                    else
                        stringBuilder.append("".repeat(maxWidth + 1));
                }
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
