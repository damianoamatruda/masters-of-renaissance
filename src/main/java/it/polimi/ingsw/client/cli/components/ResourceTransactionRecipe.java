package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.Map;

public class ResourceTransactionRecipe extends StringComponent {
    private final ReducedResourceTransactionRecipe reducedResourceTransactionRecipe;

    public ResourceTransactionRecipe(ReducedResourceTransactionRecipe reducedResourceTransactionRecipe) {
        this.reducedResourceTransactionRecipe = reducedResourceTransactionRecipe;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- Production (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---%n", reducedResourceTransactionRecipe.getId()));

        stringBuilder.append("Input:").append("\n");
        Map<String, Integer> inputResourceMap = reducedResourceTransactionRecipe.getInput();
        if (reducedResourceTransactionRecipe.getInputBlanks() > 0)
            inputResourceMap.put("Blanks", reducedResourceTransactionRecipe.getInputBlanks());
        stringBuilder.append(new ResourceMap(inputResourceMap).getString());
        if (!reducedResourceTransactionRecipe.getInputBlanksExclusions().isEmpty()) {
            stringBuilder.append("B. exclusions:").append("\n");
            reducedResourceTransactionRecipe.getInputBlanksExclusions().forEach(e -> stringBuilder.append("  ").append(new Resource(e).getString()).append("\n"));
        }

        stringBuilder.append("Output:").append("\n");
        Map<String, Integer> outputResourceMap = reducedResourceTransactionRecipe.getOutput();
        if (reducedResourceTransactionRecipe.getOutputBlanks() > 0)
            outputResourceMap.put("Blanks", reducedResourceTransactionRecipe.getOutputBlanks());
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
