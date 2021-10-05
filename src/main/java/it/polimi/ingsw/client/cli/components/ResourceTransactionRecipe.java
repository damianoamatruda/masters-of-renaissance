package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.boldColor;

/** Cli component that gives a string representation a production. */
public class ResourceTransactionRecipe extends StringComponent {
    private final ReducedResourceTransactionRecipe reducedResourceTransactionRecipe;

    public ResourceTransactionRecipe(ReducedResourceTransactionRecipe reducedResourceTransactionRecipe) {
        this.reducedResourceTransactionRecipe = reducedResourceTransactionRecipe;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Production (ID: %s) ---%n",
                boldColor(String.valueOf(reducedResourceTransactionRecipe.getId()), "\u001B[37m")));

        stringBuilder.append("Input:").append("\n");
        Map<String, Integer> inputResourceMap = reducedResourceTransactionRecipe.getInput();
        if (reducedResourceTransactionRecipe.getInputBlanks() > 0)
            inputResourceMap.put("Blank", reducedResourceTransactionRecipe.getInputBlanks());
        stringBuilder.append(new ResourceMap(inputResourceMap).getString());
        if (!reducedResourceTransactionRecipe.getInputBlanksExclusions().isEmpty()) {
            stringBuilder.append("B. exclusions:").append("\n");
            reducedResourceTransactionRecipe.getInputBlanksExclusions().forEach(e -> stringBuilder.append("  ").append(new Resource(e).getString()).append("\n"));
        }

        stringBuilder.append("Output:").append("\n");
        Map<String, Integer> outputResourceMap = reducedResourceTransactionRecipe.getOutput();
        if (reducedResourceTransactionRecipe.getOutputBlanks() > 0)
            outputResourceMap.put("Blank", reducedResourceTransactionRecipe.getOutputBlanks());
        stringBuilder.append(new ResourceMap(outputResourceMap).getString());
        if (!reducedResourceTransactionRecipe.getOutputBlanksExclusions().isEmpty()) {
            stringBuilder.append("B. exclusions:").append("\n");
            reducedResourceTransactionRecipe.getOutputBlanksExclusions().forEach(e -> stringBuilder.append("  ").append(new Resource(e).getString()).append("\n"));
        }

        if (reducedResourceTransactionRecipe.isDiscardableOutput())
            stringBuilder.append("Output is discardable");

        return stringBuilder.toString();
    }
}
