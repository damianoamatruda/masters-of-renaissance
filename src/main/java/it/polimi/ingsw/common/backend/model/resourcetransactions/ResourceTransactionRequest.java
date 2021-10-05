package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionReplacementsException.ReplacementReason;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer.*;

/**
 * This class represents a resource transaction request.
 */
public class ResourceTransactionRequest {
    private final ResourceTransactionRecipe recipe;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers;
    private final Map<ResourceType, Integer> inputNonStorableRep;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers;
    private final Map<ResourceType, Integer> outputNonStorableRep;

    /**
     * Initializes a resource transaction request.
     *
     * @param recipe               the recipe to use
     * @param inputContainers      the map of the resource containers from which to remove the input storable resources
     * @param inputNonStorableRep  the map of the non-storable resources chosen as replacement for blanks in input
     * @param outputContainers     the map of the resource containers into which to add the output storable resources
     * @param outputNonStorableRep the map of the non-storable resources chosen as replacement for blanks in output
     * @throws IllegalResourceTransactionReplacementsException if the request's replacements are invalid
     * @throws IllegalResourceTransactionContainersException   if the request's container-resource mappings are invalid
     */
    public ResourceTransactionRequest(ResourceTransactionRecipe recipe,
                                      Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                                      Map<ResourceType, Integer> inputNonStorableRep,
                                      Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers,
                                      Map<ResourceType, Integer> outputNonStorableRep) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        validate(recipe, inputContainers, inputNonStorableRep, outputContainers, outputNonStorableRep);

        this.recipe = recipe;
        this.inputContainers = sanitizeContainerMap(inputContainers);
        this.inputNonStorableRep = sanitizeResourceMap(inputNonStorableRep);
        this.outputContainers = sanitizeContainerMap(outputContainers);
        this.outputNonStorableRep = sanitizeResourceMap(outputNonStorableRep);
    }

    public static void validateInputResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateResourceMap(resMap);
        } catch (IllegalArgumentException e) {
            throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.NEGATIVE_VALUES);
        }

        if (!resMap.keySet().stream().allMatch(r -> r.isStorable() || r.isTakeableFromPlayer()))
            throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.ILLEGAL_NON_STORABLE);
    }

    public static void validateOutputResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateResourceMap(resMap);
        } catch (IllegalArgumentException e) {
            throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.NEGATIVE_VALUES);
        }

        if (!resMap.keySet().stream().allMatch(r -> r.isStorable() || r.isGiveableToPlayer()))
            throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.ILLEGAL_NON_STORABLE);
    }

    public static void validateInputStorableResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateStorableResourceMap(resMap, true);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("STORABLE_EXCEPTION"))
                throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.ILLEGAL_NON_STORABLE);

            throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.NEGATIVE_VALUES);
        }

        validateInputResourceMap(resMap);
    }

    public static void validateInputNonStorableResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateStorableResourceMap(resMap, false);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("STORABLE_EXCEPTION"))
                throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.ILLEGAL_STORABLE);

            throw new IllegalResourceTransactionReplacementsException(true, false, ReplacementReason.NEGATIVE_VALUES);
        }

        validateInputResourceMap(resMap);
    }

    public static void validateOutputStorableResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateStorableResourceMap(resMap, true);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("STORABLE_EXCEPTION"))
                throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.ILLEGAL_NON_STORABLE);

            throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.NEGATIVE_VALUES);
        }

        validateOutputResourceMap(resMap);
    }

    public static void validateOutputNonStorableResourceMap(Map<ResourceType, Integer> resMap) throws IllegalResourceTransactionReplacementsException {
        try {
            validateStorableResourceMap(resMap, false);
            validateOutputResourceMap(resMap);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("STORABLE_EXCEPTION"))
                throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.ILLEGAL_STORABLE);

            throw new IllegalResourceTransactionReplacementsException(false, false, ReplacementReason.NEGATIVE_VALUES);
        }
    }

    public static void validateContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) {
        if (containerMap == null)
            throw new NullPointerException();

        for (Map<ResourceType, Integer> resMap : containerMap.values())
            validateStorableResourceMap(resMap, true);
    }

    public static void validateInputContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) throws IllegalResourceTransactionReplacementsException {
        if (containerMap == null)
            throw new NullPointerException();

        for (Map<ResourceType, Integer> resMap : containerMap.values())
            validateInputStorableResourceMap(resMap);
    }

    public static void validateOutputContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) throws IllegalResourceTransactionReplacementsException {
        if (containerMap == null)
            throw new NullPointerException();

        for (Map<ResourceType, Integer> resMap : containerMap.values())
            validateOutputStorableResourceMap(resMap);
    }

    public static Map<ResourceContainer, Map<ResourceType, Integer>> sanitizeContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) {
        validateContainerMap(containerMap);
        return new HashMap<>(containerMap.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> sanitizeResourceMap(e.getValue()))));
    }

    /**
     * Merges two container maps.
     *
     * @return a merged map of containers
     */
    public static Map<ResourceContainer, Map<ResourceType, Integer>> mergeContainerMaps(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap1,
                                                                                        Map<ResourceContainer, Map<ResourceType, Integer>> containerMap2) {
        Map<ResourceContainer, Map<ResourceType, Integer>> containerMap = new HashMap<>(containerMap1);
        containerMap2.forEach((r, q) -> containerMap.merge(r, q, ResourceContainer::mergeResourceMaps));
        return Map.copyOf(containerMap);
    }

    /**
     * Merges container maps.
     *
     * @return a merged map of containers
     */
    public static Map<ResourceContainer, Map<ResourceType, Integer>> mergeContainerMaps(List<Map<ResourceContainer, Map<ResourceType, Integer>>> containerMaps) {
        return containerMaps.stream().reduce(ResourceTransactionRequest::mergeContainerMaps).orElse(Map.of());
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
                                 Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                                 Map<ResourceType, Integer> inputNonStorableRep,
                                 Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers,
                                 Map<ResourceType, Integer> outputNonStorableRep) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        if (recipe == null)
            throw new NullPointerException();

        validateInputContainerMap(inputContainers);
        inputContainers = sanitizeContainerMap(inputContainers);

        validateInputNonStorableResourceMap(inputNonStorableRep);
        inputNonStorableRep = sanitizeResourceMap(inputNonStorableRep);

        validateOutputContainerMap(outputContainers);
        outputContainers = sanitizeContainerMap(outputContainers);

        validateOutputNonStorableResourceMap(outputNonStorableRep);
        outputNonStorableRep = sanitizeResourceMap(outputNonStorableRep);

        if (!inputNonStorableRep.keySet().stream().allMatch(resType -> !resType.isStorable() && resType.isTakeableFromPlayer()))
            throw new IllegalResourceTransactionReplacementsException(true, true, ReplacementReason.ILLEGAL_STORABLE);

        if (!outputNonStorableRep.keySet().stream().allMatch(resType -> !resType.isStorable() && resType.isGiveableToPlayer()))
            throw new IllegalResourceTransactionReplacementsException(false, true, ReplacementReason.ILLEGAL_STORABLE);

        if (inputNonStorableRep.keySet().stream().anyMatch(resType -> recipe.getInputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException(true, true, ReplacementReason.EXCLUDED);

        if (outputNonStorableRep.keySet().stream().anyMatch(resType -> recipe.getOutputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException(false, true, ReplacementReason.EXCLUDED);

        int inputNonStorableRepCount = inputNonStorableRep.values().stream().reduce(0, Integer::sum);
        checkContainers(true, recipe.getInput(), recipe.getInputBlanks() - inputNonStorableRepCount, recipe.getInputBlanksExclusions(), inputContainers, false);

        int outputNonStorableRepCount = outputNonStorableRep.values().stream().reduce(0, Integer::sum);
        checkContainers(false, recipe.getOutput(), recipe.getOutputBlanks() - outputNonStorableRepCount, recipe.getOutputBlanksExclusions(), outputContainers, recipe.hasDiscardableOutput());
    }

    /**
     * Checks that resource containers are given for respectively all non-storable resources in a given map.
     *
     * @param resourceMap   the map of resources
     * @param resContainers the map of the resource containers to use for all the resources
     * @throws IllegalResourceTransactionContainersException if the request's container-resource mappings are invalid
     */
    private static void checkContainers(boolean isInput,
                                        Map<ResourceType, Integer> resourceMap,
                                        int blanksCount,
                                        Set<ResourceType> blanksExclusions,
                                        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers,
                                        boolean discardable) throws IllegalResourceTransactionContainersException {
        /* Filter the storable resources */
        resourceMap = resourceMap.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* Check that the map of resources contains the necessary resource types */
        if (!discardable && !resContainers.values().stream().flatMap(resMap -> resMap.keySet().stream()).collect(Collectors.toSet()).containsAll(resourceMap.keySet()))
            throw new IllegalResourceTransactionContainersException(isInput, "", 0, 0, true);

        /* Check that the map of resource containers contains the right quantities of resources */
        int resourceMapResourcesCount = resourceMap.values().stream().reduce(0, Integer::sum);
        int resContainersResourcesCount = resContainers.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (resContainersResourcesCount > resourceMapResourcesCount + blanksCount || resContainersResourcesCount < resourceMapResourcesCount + blanksCount && !discardable)
            throw new IllegalResourceTransactionContainersException(isInput, "", resourceMapResourcesCount, resContainersResourcesCount, false);

        /* Check that the leftover resources can be chosen as blanks */
        for (ResourceType resType : resourceMap.keySet()) {
            int resourceCount = resContainers.values().stream().mapToInt(containerResMap -> containerResMap.getOrDefault(resType, 0)).sum();
            if (resourceCount > resourceMap.get(resType) && blanksExclusions.contains(resType))
                throw new IllegalResourceTransactionContainersException(isInput, resType.getName(), resourceMap.get(resType), resourceCount, false);
        }
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

    /**
     * Builds an input non-storable resource map with replaced blanks.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public Map<ResourceType, Integer> getInputNonStorable() {
        return mergeResourceMaps(recipe.getInput().entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)), inputNonStorableRep);
    }

    /**
     * Builds an output non-storable resource map with replaced blanks and without discarded resources.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public Map<ResourceType, Integer> getOutputNonStorable() {
        return mergeResourceMaps(recipe.getOutput().entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)), outputNonStorableRep);
    }

    /**
     * Returns the requested output storable resources to be discarded.
     *
     * @return the output storable resources to be discarded
     */
    public Map<ResourceType, Integer> getDiscardedOutput() {
        Map<ResourceType, Integer> chosenOutputStorable = recipe.getOutput().entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<ResourceType, Integer> discardedResources = new HashMap<>(chosenOutputStorable);
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                discardedResources.computeIfPresent(resType, (r, q) -> q.equals(outputContainers.get(resContainer).get(resType)) ? null : q - outputContainers.get(resContainer).get(resType));
        return Map.copyOf(discardedResources);
    }
}
