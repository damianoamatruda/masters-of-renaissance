package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainersSanitized = sanitizeContainerMap(inputContainers);
        Map<ResourceType, Integer> inputNonStorableRepSanitized = ResourceContainer.sanitizeResourceMap(inputNonStorableRep);
        Map<ResourceContainer, Map<ResourceType, Integer>> outputContainersSanitized = sanitizeContainerMap(outputContainers);
        Map<ResourceType, Integer> outputNonStorableRepSanitized = ResourceContainer.sanitizeResourceMap(outputNonStorableRep);

        validate(recipe, inputContainersSanitized, outputContainersSanitized, inputNonStorableRepSanitized, outputNonStorableRepSanitized);

        this.recipe = recipe;
        this.inputContainers = Map.copyOf(inputContainersSanitized);
        this.inputNonStorableRep = Map.copyOf(inputNonStorableRepSanitized);
        this.outputContainers = Map.copyOf(outputContainersSanitized);
        this.outputNonStorableRep = Map.copyOf(inputNonStorableRepSanitized);
    }

    public static void validateContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) {
        if (containerMap == null)
            throw new NullPointerException();

        for (Map<ResourceType, Integer> resMap : containerMap.values())
            ResourceContainer.validateResourceMap(resMap);
    }

    public static Map<ResourceContainer, Map<ResourceType, Integer>> sanitizeContainerMap(Map<ResourceContainer, Map<ResourceType, Integer>> containerMap) {
        validateContainerMap(containerMap);
        return new HashMap<>(containerMap.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> ResourceContainer.sanitizeResourceMap(e.getValue()))));
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
                                 Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers,
                                 Map<ResourceType, Integer> inputNonStorableRep,
                                 Map<ResourceType, Integer> outputNonStorableRep) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        if (!inputNonStorableRep.keySet().stream().allMatch(resType -> !resType.isStorable() && resType.isTakeableFromPlayer()))
            throw new IllegalResourceTransactionReplacementsException(true, true, false, 0, 0);

        if (!outputNonStorableRep.keySet().stream().allMatch(resType -> !resType.isStorable() && resType.isGiveableToPlayer()))
            throw new IllegalResourceTransactionReplacementsException(false, true, false, 0, 0);

        if (inputNonStorableRep.keySet().stream().anyMatch(resType -> recipe.getInputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException(true, false, true, 0, 0);

        if (outputNonStorableRep.keySet().stream().anyMatch(resType -> recipe.getOutputBlanksExclusions().contains(resType)))
            throw new IllegalResourceTransactionReplacementsException(false, false, true, 0, 0);

        int inputNonStorableRepCount = inputNonStorableRep.values().stream().reduce(0, Integer::sum);
        checkContainers(recipe.getInput(), recipe.getInputBlanks() - inputNonStorableRepCount, recipe.getInputBlanksExclusions(), inputContainers, false);

        int outputNonStorableRepCount = outputNonStorableRep.values().stream().reduce(0, Integer::sum);
        checkContainers(recipe.getOutput(), recipe.getOutputBlanks() - outputNonStorableRepCount, recipe.getOutputBlanksExclusions(), outputContainers, recipe.hasDiscardableOutput());
    }

    /**
     * Checks that resource containers are given for respectively all non-storable resources in a given map.
     *
     * @param resourceMap   the map of resources
     * @param resContainers the map of the resource containers to use for all the resources
     * @throws IllegalResourceTransactionContainersException if the request's container-resource mappings are invalid
     */
    private static void checkContainers(Map<ResourceType, Integer> resourceMap,
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
            throw new IllegalResourceTransactionContainersException("", 0, 0, false); // TODO: These are dummy parameter values

        /* Check that the map of resource containers contains the right quantities of resources */
        int resourceMapResourcesCount = resourceMap.values().stream().reduce(0, Integer::sum);
        int resContainersResourcesCount = resContainers.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (resContainersResourcesCount > resourceMapResourcesCount + blanksCount || resContainersResourcesCount < resourceMapResourcesCount + blanksCount && !discardable)
            throw new IllegalResourceTransactionContainersException("", resourceMapResourcesCount, resContainersResourcesCount, false);

        /* Check that the leftover resources can be chosen as blanks */
        for (ResourceType resType : resourceMap.keySet()) {
            int resourceCount = resContainers.values().stream().mapToInt(containerResMap -> containerResMap.getOrDefault(resType, 0)).sum();
            if (resourceCount > resourceMap.get(resType) && blanksExclusions.contains(resType))
                throw new IllegalResourceTransactionContainersException(resType.getName(), resourceMap.get(resType), resourceCount, false);
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
        return ResourceContainer.mergeResourceMaps(recipe.getInput().entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)), inputNonStorableRep);
    }

    /**
     * Builds an output non-storable resource map with replaced blanks and without discarded resources.
     *
     * @return a map of chosen resources including replaced blanks
     */
    public Map<ResourceType, Integer> getOutputNonStorable() {
        return ResourceContainer.mergeResourceMaps(recipe.getOutput().entrySet().stream()
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
