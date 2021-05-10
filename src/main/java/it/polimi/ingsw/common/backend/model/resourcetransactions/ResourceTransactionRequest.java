package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a resource transaction request.
 */
public class ResourceTransactionRequest {
    private final ResourceTransactionRecipe recipe;
    private final Map<ResourceType, Integer> inputBlanksRep;
    private final Map<ResourceType, Integer> outputBlanksRep;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers;

    /**
     * Initializes a resource transaction request.
     *
     * @param recipe           the recipe to use
     * @param inputBlanksRep   the map of the resources chosen as replacement for blanks in input
     * @param outputBlanksRep  the map of the resources chosen as replacement for blanks in output
     * @param inputContainers  the map of the resource containers from which to remove the input storable resources
     * @param outputContainers the map of the resource containers into which to add the output storable resources
     * @throws IllegalResourceTransactionReplacementsException if the request's replacements are invalid
     * @throws IllegalResourceTransactionContainersException   if the request's container-resource mappings are invalid
     */
    public ResourceTransactionRequest(ResourceTransactionRecipe recipe,
                                      Map<ResourceType, Integer> inputBlanksRep,
                                      Map<ResourceType, Integer> outputBlanksRep,
                                      Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                                      Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) {
        validate(recipe, inputBlanksRep, outputBlanksRep, inputContainers, outputContainers);
        this.recipe = recipe;
        this.inputBlanksRep = Map.copyOf(inputBlanksRep);
        this.outputBlanksRep = Map.copyOf(outputBlanksRep);
        this.inputContainers = Map.copyOf(inputContainers);
        this.outputContainers = Map.copyOf(outputContainers);
    }

    /**
     * Returns whether the resource transaction request is valid, i.e.:
     * <ol>
     *     <li>maps of input and output replacement resources do not contain respectively takeable-only and
     *         giveable-only resources</li>
     *     <li>maps of input and output replacement resources do not contain excluded resources</li>
     *     <li>maps of input and output replacement resources have the same number of resources as input and output
     *         blanks</li>
     *     <li>maps of discarded output resources are given only if recipe allows discardable output</li>
     *     <li>maps of input and output resource containers are given for respectively all non-storable resources in
     *         input and output</li>
     * </ol>
     *
     * @throws IllegalResourceTransactionReplacementsException if the request's replacements are invalid
     * @throws IllegalResourceTransactionContainersException   if the request's container-resource mappings are invalid
     */
    private static void validate(ResourceTransactionRecipe recipe,
                                 Map<ResourceType, Integer> inputBlanksRep,
                                 Map<ResourceType, Integer> outputBlanksRep,
                                 Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                                 Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        // TODO: Check that the given maps have values >= 0

        if (inputBlanksRep.keySet().stream().anyMatch(resType -> !resType.isStorable() && !resType.isTakeableFromPlayer()))
            throw new IllegalResourceTransactionReplacementsException("Input replacements contain non-storable and non-takeable resources", inputBlanksRep);
        if (outputBlanksRep.keySet().stream().anyMatch(resType -> !resType.isStorable() && !resType.isGiveableToPlayer()))
            throw new IllegalResourceTransactionReplacementsException("Output replacements contain non-storable and non-giveable resources", outputBlanksRep);

        if (inputBlanksRep.keySet().stream().anyMatch(resType -> recipe.getInputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException("Input replacements contain excluded resources", inputBlanksRep);
        if (outputBlanksRep.keySet().stream().anyMatch(resType -> recipe.getOutputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException("Output replacements contain excluded resources", outputBlanksRep);

        if (inputBlanksRep.values().stream().reduce(0, Integer::sum) != recipe.getInputBlanks())
            throw new IllegalResourceTransactionReplacementsException(String.format("Incorrect input replacements' amount, should be %d", recipe.getInputBlanks()), inputBlanksRep);
        if (outputBlanksRep.values().stream().reduce(0, Integer::sum) != recipe.getOutputBlanks())
            throw new IllegalResourceTransactionReplacementsException(String.format("Incorrect input replacements' amount, should be %d", recipe.getInputBlanks()), inputBlanksRep);

        if (!recipe.hasDiscardableOutput() && !getDiscardedOutput(recipe.getOutput(), outputBlanksRep, outputContainers).isEmpty())
            throw new RuntimeException(); // TODO: Add more specific exception

        checkContainers(getReplacedInput(recipe.getInput(), inputBlanksRep), inputContainers);
        checkContainers(getReplacedOutput(recipe.getOutput(), outputBlanksRep), outputContainers);
    }

    /**
     * Checks that resource containers are given for respectively all non-storable resources in a given map.
     *
     * @param resourceMap   the map of resources
     * @param resContainers the map of the resource containers to use for all the resources
     * @throws IllegalResourceTransactionContainersException if the request's container-resource mappings are invalid
     */
    private static void checkContainers(Map<ResourceType, Integer> resourceMap,
                                        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalResourceTransactionContainersException {
        /* Filter the storable resources */
        resourceMap = resourceMap.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* Check that the map of resources and the map of resource containers contain the same number of storable
           resources */
        int resourceMapResourcesCount = resourceMap.values().stream().reduce(0, Integer::sum);
        int resContainersResourcesCount = resContainers.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (resContainersResourcesCount != resourceMapResourcesCount)
            throw new IllegalResourceTransactionContainersException(
                    String.format("Amount of resources specified in the shelves map (%d) does not match amount in replaced recipe (%d)",
                            resContainersResourcesCount, resourceMapResourcesCount));

        /* Check that the quantity of each storable resource type in the map of resources is the same as in the map
           of resource containers */
        for (ResourceType resType : resourceMap.keySet()) {
            int resourceCount = 0;
            for (ResourceContainer resContainer : resContainers.keySet())
                resourceCount += resContainers.get(resContainer).getOrDefault(resType, 0);
            if (resourceCount != resourceMap.get(resType))
                throw new IllegalResourceTransactionContainersException(
                        String.format("Amount of %s specified in replaced recipe (%d) does not match amount specified in shelves (%d)",
                                resType.getName(), resourceMap.get(resType), resourceCount));
        }
    }

    /**
     * Builds an input resource map with replaced blanks.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public static Map<ResourceType, Integer> getReplacedInput(Map<ResourceType, Integer> recipeInput,
                                                              Map<ResourceType, Integer> inputBlanksRep) {
        Map<ResourceType, Integer> replacedInput = new HashMap<>(recipeInput);
        inputBlanksRep.forEach((r, q) -> replacedInput.merge(r, q, Integer::sum));
        return Map.copyOf(replacedInput);
    }

    /**
     * Builds an output resource map with replaced blanks and without discarded resources.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public static Map<ResourceType, Integer> getReplacedOutput(Map<ResourceType, Integer> recipeOutput,
                                                               Map<ResourceType, Integer> outputBlanksRep) {
        Map<ResourceType, Integer> replacedOutput = new HashMap<>(recipeOutput);
        outputBlanksRep.forEach((r, q) -> replacedOutput.merge(r, q, Integer::sum));
        return Map.copyOf(replacedOutput);
    }

    /**
     * Returns the requested output storable resources to be discarded.
     *
     * @return the output storable resources to be discarded
     */
    public static Map<ResourceType, Integer> getDiscardedOutput(Map<ResourceType, Integer> recipeOutput,
                                                                Map<ResourceType, Integer> outputBlanksRep,
                                                                Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) {
        Map<ResourceType, Integer> chosenOutput = new HashMap<>(recipeOutput);
        outputBlanksRep.forEach((r, q) -> chosenOutput.merge(r, q, Integer::sum));
        Map<ResourceType, Integer> chosenOutputStorable = chosenOutput.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<ResourceType, Integer> discardedResources = new HashMap<>(chosenOutputStorable);
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                discardedResources.computeIfPresent(resType, (r, q) -> q.equals(outputContainers.get(resContainer).get(resType)) ? null : q - outputContainers.get(resContainer).get(resType));
        return discardedResources;
    }

    /**
     * Returns the requested recipe.
     *
     * @return the recipe
     */
    public ResourceTransactionRecipe getRecipe() {
        return recipe;
    }

    /**
     * Builds an input resource map with replaced blanks.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public Map<ResourceType, Integer> getReplacedInput() {
        return getReplacedInput(recipe.getInput(), inputBlanksRep);
    }

    /**
     * Builds an output resource map with replaced blanks and without discarded resources.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public Map<ResourceType, Integer> getReplacedOutput() {
        return getReplacedOutput(recipe.getOutput(), outputBlanksRep);
    }

    /**
     * Returns the requested output storable resources to be discarded.
     *
     * @return the output storable resources to be discarded
     */
    public Map<ResourceType, Integer> getDiscardedOutput() {
        return getDiscardedOutput(recipe.getOutput(), outputBlanksRep, outputContainers);
    }

    /**
     * Returns the requested input resource containers.
     *
     * @return the input resource containers
     */
    public Map<ResourceContainer, Map<ResourceType, Integer>> getInputContainers() {
        return inputContainers;
    }

    /**
     * Returns the requested output resource containers.
     *
     * @return the output resource containers
     */
    public Map<ResourceContainer, Map<ResourceType, Integer>> getOutputContainers() {
        return outputContainers;
    }
}
