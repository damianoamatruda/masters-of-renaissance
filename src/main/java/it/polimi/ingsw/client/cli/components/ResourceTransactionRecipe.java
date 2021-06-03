package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.ArrayList;
import java.util.List;

public class ResourceTransactionRecipe extends StringComponent {
    private final ReducedResourceTransactionRecipe reducedResourceTransactionRecipe;

    public ResourceTransactionRecipe(ReducedResourceTransactionRecipe reducedResourceTransactionRecipe) {
        this.reducedResourceTransactionRecipe = reducedResourceTransactionRecipe;
    }

    @Override
    public String getString(Cli cli) {
        List<String> column = new ArrayList<>();

        column.add(String.format("--- Production (ID: %d) ---", reducedResourceTransactionRecipe.getId()));

        String row;

        column.add("Input:");
        row = new ResourceMap(reducedResourceTransactionRecipe.getInput()).getString(cli);
        if (reducedResourceTransactionRecipe.getInputBlanks() > 0) {
            row += String.format("  %-23s", String.format("Blanks: %d", reducedResourceTransactionRecipe.getInputBlanks()));
            column.add(row);
        } else if (row.length() > 0)
            column.add(row + " ".repeat(25));
        if (!reducedResourceTransactionRecipe.getInputBlanksExclusions().isEmpty()) {
            column.add("B. exclusions:");
            reducedResourceTransactionRecipe.getInputBlanksExclusions().forEach(e -> column.add("  " + new Resource(e).getString(cli)));
        }

        column.add("Output:");
        row = new ResourceMap(reducedResourceTransactionRecipe.getOutput()).getString(cli);
        if (reducedResourceTransactionRecipe.getOutputBlanks() > 0) {
            row += String.format("  %-23s", String.format("Blanks: %d", reducedResourceTransactionRecipe.getOutputBlanks()));
            column.add(row);
        } else if (row.length() > 0)
            column.add(row + " ".repeat(25));
        if (!reducedResourceTransactionRecipe.getOutputBlanksExclusions().isEmpty()) {
            column.add("B. exclusions:");
            reducedResourceTransactionRecipe.getOutputBlanksExclusions().forEach(e -> column.add("  " + new Resource(e).getString(cli)));
        }

        column.add(reducedResourceTransactionRecipe.isDiscardableOutput() ? "Output is discardable" : " ");

        return String.join("\n", column);
    }
}
