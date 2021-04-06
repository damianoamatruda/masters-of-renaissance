package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.resourcecontainers.ResourceContainer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a production, that is a transaction of transfers of resources from and to resource containers
 * and a player.
 */
public class Production {
    /** The map of the input resources, including blanks. */
    private final Map<ResourceType, Integer> input;

    /** The map of the output resources, including blanks. */
    private final Map<ResourceType, Integer> output;

    /** True if the output can be discarded, otherwise false. */
    private final boolean discardableOutput;

    /**
     * Initializes the production specifying its input and its output.
     *
     * @param input             the map of the resources to be given as input of the production
     * @param output            the map of the resources to be taken as output of the production
     * @param discardableOutput true if the output can be discarded, otherwise false
     */
    public Production(Map<ResourceType, Integer> input, Map<ResourceType, Integer> output, boolean discardableOutput) {
        this.input = input;
        this.output = output;
        this.discardableOutput = discardableOutput;
    }

    /**
     * Initializes a production with non-discardable output specifying its input and its output.
     *
     * @param input     the map of the resources to be given as input of the production
     * @param output    the map of the resources to be taken as output of the production
     */
    public Production(Map<ResourceType, Integer> input, Map<ResourceType, Integer> output) {
        this(input, output, false);
    }

    /**
     * Initializes a production made of multiple contemporary productions.
     *
     * @param productions   the list of the productions to sum
     */
    public Production(List<Production> productions) {
        this.input = new HashMap<>();
        this.output = new HashMap<>();
        for (Production production : productions) {
            production.input.forEach((r, q) -> this.input.merge(r, q, Integer::sum));
            production.output.forEach((r, q) -> this.input.merge(r, q, Integer::sum));
        }
        this.discardableOutput = productions.stream().allMatch(production -> production.discardableOutput);
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
     * Returns the map of the output resources of the production.
     *
     * @return  the map of the output resources
     */
    public Map<ResourceType, Integer> getOutput() {
        return new HashMap<>(output);
    }

    /**
     * Returns whether the production has discardable output.
     *
     * @return  true if the output can be discarded, otherwise false
     */
    public boolean hasDiscardableOutput() {
        return discardableOutput;
    }

    /**
     * Returns whether the production is empty.
     *
     * @return  true if the production has empty input and empty output, otherwise false
     */
    public boolean isEmpty() {
        return input.isEmpty() && output.isEmpty();
    }

    /**
     * Activates the production. Replaces the blanks in input and in output with the given resources, removes the input
     * storable resources from given resource containers, adds the output storable resources into given resource
     * containers, takes the non-storable input resources from the player and gives the non-storable output resources to
     * the player.
     *
     * This is a transaction: if the transfer is unsuccessful, a checked exception is thrown and the states of the
     * player and the resource containers remain unchanged.
     *
     * @param game              the game the player is playing in
     * @param player            the player on which to trigger the actions of the non-storable resources
     * @param inputBlanksRep    the map of the resources chosen as replacement for blanks in input
     * @param outputBlanksRep   the map of the resources chosen as replacement for blanks in output
     * @param inputContainers  the map of the resource containers from which to remove the input storable resources
     * @param outputContainers the map of the resource containers into which to add the output storable resources
     * @throws Exception        if the transaction failed
     */
    public void activate(Game game, Player player,
                         Map<ResourceType, Integer> inputBlanksRep,
                         Map<ResourceType, Integer> outputBlanksRep,
                         Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                         Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) throws Exception {
        Map<ResourceType, Integer> replacedInput = replaceBlanks(input, inputBlanksRep);
        Map<ResourceType, Integer> replacedOutput = replaceBlanks(output, outputBlanksRep);

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
                        resType.removeFromContainer(clonedContainers.get(resContainer));
                    } catch (Exception e) {
                        if (!discardableOutput)
                            throw new Exception();
                    }

        /* Try adding all output storable resources into cloned resource containers (with input removed);
           if an exception is thrown, the transfer is not possible */
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                for (int i = 0; i < outputContainers.get(resContainer).get(resType); i++)
                    try {
                        resType.addIntoContainer(clonedContainers.get(resContainer));
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
                        resType.removeFromContainer(resContainer);
                    } catch (Exception e) {
                        if (discardableOutput)
                            resType.discard(game, player);
                        else
                            throw new RuntimeException();
                    }

        /* Add all output storable resources into real resource containers;
           this should be possible, as it worked with cloned resource containers */
        for (ResourceContainer resContainer : outputContainers.keySet())
            for (ResourceType resType : outputContainers.get(resContainer).keySet())
                for (int i = 0; i < outputContainers.get(resContainer).get(resType); i++)
                    try {
                        resType.addIntoContainer(resContainer);
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
     * Replaces the blank resources in a given map of resources.
     *
     * @param mapWithBlanks the map of resources including blanks
     * @param blanksRep     the map of replacement resources of blanks
     * @return              the map of resources with replaced blanks
     * @throws Exception    if it is not possible
     */
    private static Map<ResourceType, Integer> replaceBlanks(Map<ResourceType, Integer> mapWithBlanks,
                                                            Map<ResourceType, Integer> blanksRep) throws Exception {
        /* Check that blanksRep does not contain blanks */
        if (blanksRep.keySet().stream().anyMatch(ResourceType::isBlank))
            throw new Exception();

        /* Check that blanksRep has the same number of resources as blanks in mapWithBlanks */
        if (!blanksRep.isEmpty()) {
            int blanksCount = mapWithBlanks.entrySet().stream()
                    .filter(e -> e.getKey().isBlank()).map(Map.Entry::getValue).reduce(0, Integer::sum);
            int blanksRepCount = blanksRep.values().stream().reduce(0, Integer::sum);
            if (blanksRepCount != blanksCount)
                throw new Exception();
        }

        /* Add all resources from mapWithBlanks that are not blanks */
        Map<ResourceType, Integer> replacedBlanks = mapWithBlanks.entrySet().stream()
                .filter(e -> !e.getKey().isBlank())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* Add all replacement resources from blanksRep */
        blanksRep.forEach((r, q) -> replacedBlanks.merge(r, q, Integer::sum));

        return replacedBlanks;
    }

    /**
     * Checks that resource containers are given for respectively all non-storable resources in a given map.
     *
     * @param mapWithoutBlanks  the map of resources without blanks
     * @param resContainers     the map of the resource containers to use for all the resources
     * @return                  true if the resources and the quantities match, otherwise false
     */
    private static boolean checkContainers(Map<ResourceType, Integer> mapWithoutBlanks,
                                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) {
        /* Filter the storable resources */
        mapWithoutBlanks = mapWithoutBlanks.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));;

        /* Check that mapWithoutBlanks and resContainers contain the same number of storable resources */
        int mapWithoutBlanksResourcesCount = mapWithoutBlanks.values().stream().reduce(0, Integer::sum);
        int containersResourcesCount = resContainers.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (containersResourcesCount != mapWithoutBlanksResourcesCount)
            return false;

        /* Check that the amount of each storable resource type in mapWithoutBlanks is the same as in resContainers */
        for (ResourceType resType : mapWithoutBlanks.keySet()) {
            int containersResourceCount = 0;
            for (ResourceContainer resContainer : resContainers.keySet())
                containersResourceCount += resContainers.get(resContainer).getOrDefault(resType, 0);
            if (containersResourceCount != mapWithoutBlanks.get(resType))
                return false;
        }

        return true;
    }
}
