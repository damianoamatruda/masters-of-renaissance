package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a production, that is a transaction of transfers of resources from and to resource containers
 * and a player.
 */
public class Production {
    /** The map of the input resources. */
    private final Map<ResourceType, Integer> input;

    /** The number of the input blanks. */
    private final int inputBlanks;

    /** The map of the output resources. */
    private final Map<ResourceType, Integer> output;

    /** The number of the output blanks. */
    private final int outputBlanks;

    /** <code>true</code> if the output can be discarded; <code>false</code> otherwise. */
    private final boolean discardableOutput;

    /**
     * Initializes the production specifying its input and its output.
     *  @param input            the map of the resources to be given as input of the production
     * @param inputBlanks       the number of the input blanks
     * @param output            the map of the resources to be taken as output of the production
     * @param outputBlanks      the number of the output blanks
     * @param discardableOutput <code>true</code> if the output can be discarded; <code>false</code> otherwise.
     */
    public Production(Map<ResourceType, Integer> input, int inputBlanks,
                      Map<ResourceType, Integer> output, int outputBlanks,
                      boolean discardableOutput) {
        this.input = input;
        this.inputBlanks = inputBlanks;
        this.output = output;
        this.outputBlanks = outputBlanks;
        this.discardableOutput = discardableOutput;
    }

    /**
     * Initializes a production with non-discardable output specifying its input and its output.
     * @param input         the map of the resources to be given as input of the production
     * @param inputBlanks   the number of the input blanks
     * @param output        the map of the resources to be taken as output of the production
     * @param outputBlanks  the number of the input blanks
     */
    public Production(Map<ResourceType, Integer> input, int inputBlanks,
                      Map<ResourceType, Integer> output, int outputBlanks) {
        this(input, inputBlanks, output, outputBlanks, false);
    }

    /**
     * Initializes a production made of multiple contemporary productions.
     *
     * @param productions   the list of the productions to sum
     */
    public Production(List<Production> productions) {
        this.input = new HashMap<>();
        int inputBlanks = 0;
        this.output = new HashMap<>();
        int outputBlanks = 0;
        for (Production production : productions) {
            production.input.forEach((r, q) -> this.input.merge(r, q, Integer::sum));
            inputBlanks += production.inputBlanks;
            production.output.forEach((r, q) -> this.input.merge(r, q, Integer::sum));
            outputBlanks += production.outputBlanks;
        }
        this.inputBlanks = inputBlanks;
        this.outputBlanks = outputBlanks;
        this.discardableOutput = productions.stream().allMatch(production -> production.discardableOutput);
    }

    /**
     * Activates the production. Replaces the blanks in input and in output with the given resources, removes the input
     * storable resources from given resource containers, adds the output storable resources into given resource
     * containers, takes the non-storable input resources from the player and gives the non-storable output resources to
     * the player.
     * <p>
     * This is a transaction: if the transfer is unsuccessful, a checked exception is thrown and the states of the
     * player and the resource containers remain unchanged.
     *
     * @param game              the game the player is playing in
     * @param player            the player on which to trigger the actions of the non-storable resources
     * @param inputBlanksRep    the map of the resources chosen as replacement for blanks in input
     * @param outputBlanksRep   the map of the resources chosen as replacement for blanks in output
     * @param inputContainers   the map of the resource containers from which to remove the input storable resources
     * @param outputContainers  the map of the resource containers into which to add the output storable resources
     * @throws Exception        if the transaction failed
     */
    public void activate(Game game, Player player,
                         Map<ResourceType, Integer> inputBlanksRep,
                         Map<ResourceType, Integer> outputBlanksRep,
                         Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                         Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) throws Exception {
        Map<ResourceType, Integer> replacedInput = addReplacedBlanks(input, inputBlanks, inputBlanksRep);
        Map<ResourceType, Integer> replacedOutput = addReplacedBlanks(output, outputBlanks, outputBlanksRep);

        if (!checkContainers(replacedInput, inputContainers))
            throw new Exception();
        if (!checkContainers(replacedOutput, outputContainers))
            throw new Exception();

        /* Get set of all resource containers, in input and in output */
        Set<ResourceContainer> allContainers = new HashSet<>();
        allContainers.addAll(inputContainers.keySet());
        allContainers.addAll(outputContainers.keySet());

        /* Make map of clones of all resource containers */
        Map<ResourceContainer, ResourceContainer> clonedContainers = allContainers.stream()
                .collect(Collectors.toMap(Function.identity(), ResourceContainer::copy));

        /* Try removing all input storable resources from cloned resource containers;
           if an exception is thrown, the transfer is not possible */
        for (ResourceContainer resContainer : inputContainers.keySet())
            for (ResourceType resType : inputContainers.get(resContainer).keySet())
                for (int i = 0; i < inputContainers.get(resContainer).get(resType); i++)
                    try {
                        clonedContainers.get(resContainer).removeResource(resType);
                    } catch (Exception e) {
                        throw new Exception();
                    }

        /* Try adding all output storable resources into cloned resource containers (with input removed);
           if an exception is thrown, the transfer is not possible */
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                for (int i = 0; i < outputContainers.get(resContainer).get(resType); i++)
                    try {
                        clonedContainers.get(resContainer).addResource(resType);
                    } catch (Exception e) {
                        if (!discardableOutput)
                            throw new Exception();
                    }

        /* Remove all input storable resources from real resource containers;
           this should be possible, as it worked with cloned resource containers */
        for (ResourceContainer resContainer : inputContainers.keySet())
            for (ResourceType resType : inputContainers.get(resContainer).keySet())
                for (int i = 0; i < inputContainers.get(resContainer).get(resType); i++)
                    try {
                        resContainer.removeResource(resType);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }

        /* Add all output storable resources into real resource containers;
           this should be possible, as it worked with cloned resource containers */
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                for (int i = 0; i < outputContainers.get(resContainer).get(resType); i++)
                    try {
                        resContainer.addResource(resType);
                    } catch (Exception e) {
                        if (discardableOutput)
                            resType.discard(game, player);
                        else
                            throw new RuntimeException();
                    }

        /* Filter the non-storable resources */
        Map<ResourceType, Integer> nonStorableReplacedInput = replacedInput.entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<ResourceType, Integer> nonStorableReplacedOutput = replacedOutput.entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* Take all input non-storable resources from player; this is always possible */
        for (ResourceType resType : nonStorableReplacedInput.keySet())
            for (int i = 0; i < nonStorableReplacedInput.get(resType); i++)
                resType.takeFromPlayer(game, player);

        /* Give all output non-storable resources to player; this is always possible */
        for (ResourceType resType : nonStorableReplacedOutput.keySet())
            for (int i = 0; i < nonStorableReplacedOutput.get(resType); i++)
                resType.giveToPlayer(game, player);
    }

    /**
     * Builds a resource map with replaced blanks.
     *
     * @param resourceMap   the map of resources
     * @param blanks        the number of the blanks to add
     * @param blanksRep     the map of replacement resources of blanks
     * @return              a map of resources including replaced blanks
     * @throws Exception    if it is not possible
     */
    private static Map<ResourceType, Integer> addReplacedBlanks(Map<ResourceType, Integer> resourceMap, int blanks,
                                                                Map<ResourceType, Integer> blanksRep) throws Exception {
        /* Check that the map of replacement resources has the same number of resources as blanks */
        if (!blanksRep.isEmpty() && blanksRep.values().stream().reduce(0, Integer::sum) != blanks)
            throw new Exception();

        Map<ResourceType, Integer> mapWithReplacedBlanks = new HashMap<>(resourceMap);
        blanksRep.forEach((r, q) -> mapWithReplacedBlanks.merge(r, q, Integer::sum));

        return mapWithReplacedBlanks;
    }

    /**
     * Checks that resource containers are given for respectively all non-storable resources in a given map.
     *
     * @param resourceMap   the map of resources
     * @param resContainers the map of the resource containers to use for all the resources
     * @return              <code>true</code> if the resources and the quantities match; <code>false</code> otherwise.
     */
    private static boolean checkContainers(Map<ResourceType, Integer> resourceMap,
                                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) {
        /* Filter the storable resources */
        resourceMap = resourceMap.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* Check that mapWithoutBlanks and resContainers contain the same number of storable resources */
        int mapWithoutBlanksResourcesCount = resourceMap.values().stream().reduce(0, Integer::sum);
        int containersResourcesCount = resContainers.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (containersResourcesCount != mapWithoutBlanksResourcesCount)
            return false;

        /* Check that the amount of each storable resource type in mapWithoutBlanks is the same as in resContainers */
        for (ResourceType resType : resourceMap.keySet()) {
            int containersResourceCount = 0;
            for (ResourceContainer resContainer : resContainers.keySet())
                containersResourceCount += resContainers.get(resContainer).getOrDefault(resType, 0);
            if (containersResourceCount != resourceMap.get(resType))
                return false;
        }

        return true;
    }

    /**
     * Returns the map of the input resources of the production.
     *
     * @return  the map of the input resources
     */
    public Map<ResourceType, Integer> getInput() {
        return new HashMap<>(input);
    }

    /**
     * Returns the number of the input blanks of the production.
     *
     * @return  the number of the input blanks
     */
    public int getInputBlanks() {
        return inputBlanks;
    }

    /**
     * Returns the map of the output resources of the production.
     *
     * @return  the map of the output resources
     */
    public Map<ResourceType, Integer> getOutput() {
        return new HashMap<>(output);
    }

    /**
     * Returns the number of the output blanks of the production.
     *
     * @return  the number of the output blanks
     */
    public int getOutputBlanks() {
        return outputBlanks;
    }

    /**
     * Returns whether the production has discardable output.
     *
     * @return  <code>true</code> if the output can be discarded; <code>false</code> otherwise.
     */
    public boolean hasDiscardableOutput() {
        return discardableOutput;
    }

    /**
     * Returns whether the production is empty.
     *
     * @return  <code>true</code> if the production has empty input and empty output; <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return input.isEmpty() && output.isEmpty();
    }
}
