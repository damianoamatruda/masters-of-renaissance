package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

public class ResourceTransactionRecipe extends StringComponent {
    private final ReducedResourceTransactionRecipe reducedResourceTransactionRecipe;

    public ResourceTransactionRecipe(ReducedResourceTransactionRecipe reducedResourceTransactionRecipe) {
        this.reducedResourceTransactionRecipe = reducedResourceTransactionRecipe;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Production (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---%n", reducedResourceTransactionRecipe.getId()));

        stringBuilder.append("Input:").append("\n");
        stringBuilder.append(new ResourceMap(reducedResourceTransactionRecipe.getInput()).getString(cli));
        if (reducedResourceTransactionRecipe.getInputBlanks() > 0)
            stringBuilder.append(String.format("  Blanks: %d", reducedResourceTransactionRecipe.getInputBlanks())).append("\n");
        if (!reducedResourceTransactionRecipe.getInputBlanksExclusions().isEmpty()) {
            stringBuilder.append("B. exclusions:").append("\n");
            reducedResourceTransactionRecipe.getInputBlanksExclusions().forEach(e -> stringBuilder.append("  ").append(new Resource(e).getString(cli)).append("\n"));
        }

        stringBuilder.append("Output:").append("\n");
        stringBuilder.append(new ResourceMap(reducedResourceTransactionRecipe.getOutput()).getString(cli));
        if (reducedResourceTransactionRecipe.getOutputBlanks() > 0)
            stringBuilder.append(String.format("  Blanks: %d", reducedResourceTransactionRecipe.getOutputBlanks())).append("\n");
        if (!reducedResourceTransactionRecipe.getOutputBlanksExclusions().isEmpty()) {
            stringBuilder.append("B. exclusions:").append("\n");
            reducedResourceTransactionRecipe.getOutputBlanksExclusions().forEach(e -> stringBuilder.append("  ").append(new Resource(e).getString(cli)).append("\n"));
        }

        if (reducedResourceTransactionRecipe.isDiscardableOutput())
            stringBuilder.append("Output is discardable");

        return stringBuilder.toString();
    }
}
