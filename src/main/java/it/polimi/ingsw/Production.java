package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class representing a production of resources.
 */
public class Production {
    /** The map of the resources, including blanks, to be given as input. */
    private final Map<ResourceType, Integer> input;

    /** The map of the resources, including blanks, to be taken as output. */
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
     * Initializes the production (with non-discardable output) specifying its input and its output.
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
     * Returns the map of the resources to be given as input of the production.
     *
     * @return  the map of the resources to be given as input
     */
    public Map<ResourceType, Integer> getInput() {
        return new HashMap<>(input);
    }

    /**
     * Returns the map of the resources to be taken as output of the production.
     *
     * @return  the map of the resources to be taken as output
     */
    public Map<ResourceType, Integer> getOutput() {
        return new HashMap<>(output);
    }

    /**
     * Returns whether the production has discardable output.
     *
     * @return  true if has discardable output, false otherwise
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
     * Activates the production. Replaces the blanks in input and in output, if applicable, with the given resources,
     * removes the input resources from the given strongboxes and adds the output resources in the given strongbox.
     *
     * All resources have to be transferred in a single transaction.
     *
     * @param game              the game the player is playing in
     * @param player            the player on which to trigger the action of the resource, if applicable
     * @param inputBlanksRep    the map of the resources to be given as replacement for blanks in input
     * @param outputBlanksRep   the map of the resources to be taken as replacement for blanks in output
     * @param inputStrongboxes  the map of the strongboxes to use for all the resources to be given as input
     * @param outputStrongboxes the map of the strongboxes to use for all the resources to be taken as output
     * @throws Exception        if it is not possible
     */
    public void activate(Game game, Player player,
                         Map<ResourceType, Integer> inputBlanksRep,
                         Map<ResourceType, Integer> outputBlanksRep,
                         Map<Strongbox, Map<ResourceType, Integer>> inputStrongboxes,
                         Map<Strongbox, Map<ResourceType, Integer>> outputStrongboxes) throws Exception {
        Map<ResourceType, Integer> replacedInput = replaceBlanks(input, inputBlanksRep);
        Map<ResourceType, Integer> replacedOutput = replaceBlanks(output, outputBlanksRep);

        if (!checkStrongboxes(replacedInput, inputStrongboxes))
            throw new Exception();
        if (!checkStrongboxes(replacedOutput, outputStrongboxes))
            throw new Exception();

        if (!discardableOutput) {
            /* Get set of all strongboxes, in input and in output */
            Set<Strongbox> allStrongboxes = new HashSet<>();
            allStrongboxes.addAll(inputStrongboxes.keySet());
            allStrongboxes.addAll(outputStrongboxes.keySet());

            /* Make map of clones of all strongboxes */
            Map<Strongbox, Strongbox> clonedStrongboxes = allStrongboxes.stream()
                    .collect(Collectors.toMap(Function.identity(), Strongbox::copy));

            /* Try removing all input storable resources from cloned strongboxes;
               if exception is thrown, the production is not possible (only cloned strongboxes are touched) */
            for (Strongbox strongbox : inputStrongboxes.keySet())
                for (ResourceType resType : inputStrongboxes.get(strongbox).keySet())
                    for (int i = 0; i < inputStrongboxes.get(strongbox).get(resType); i++)
                        try {
                            resType.removeFromStrongbox(clonedStrongboxes.get(strongbox));
                        } catch (Exception e) {
                            throw new Exception();
                        }

            /* Try adding all output storable resources into cloned strongboxes (with input removed);
               if exception is thrown, the production is not possible (only cloned strongboxes are touched) */
            for (Strongbox strongbox : outputStrongboxes.keySet())
                for (ResourceType resType : outputStrongboxes.get(strongbox).keySet())
                    for (int i = 0; i < outputStrongboxes.get(strongbox).get(resType); i++)
                        try {
                            resType.addIntoStrongbox(clonedStrongboxes.get(strongbox));
                        } catch (Exception e) {
                            throw new Exception();
                        }
        }

        /* Remove all input storable resources from real strongboxes;
           this should be possible, as it worked with cloned strongboxes */
        for (Strongbox strongbox : inputStrongboxes.keySet())
            for (ResourceType resType : inputStrongboxes.get(strongbox).keySet())
                for (int i = 0; i < inputStrongboxes.get(strongbox).get(resType); i++)
                    try {
                        resType.removeFromStrongbox(strongbox);
                    } catch (Exception e) {
                        if (discardableOutput)
                            resType.discard(game, player, strongbox);
                        else
                            throw new RuntimeException();
                    }

        /* Add all output storable resources into real strongboxes;
           this should be possible, as it worked with cloned strongboxes */
        for (Strongbox strongbox : outputStrongboxes.keySet())
            for (ResourceType resType : outputStrongboxes.get(strongbox).keySet())
                for (int i = 0; i < outputStrongboxes.get(strongbox).get(resType); i++)
                    try {
                        resType.addIntoStrongbox(strongbox);
                    } catch (Exception e) {
                        if (discardableOutput)
                            resType.discard(game, player, strongbox);
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
     * Replaces the blank resources in a given map.
     *
     * @param mapWithBlanks full map including blanks
     * @param blanksRep     map of replacement of blanks
     * @return              map with replaced blanks
     * @throws Exception    if it is not possible
     */
    private static Map<ResourceType, Integer> replaceBlanks(Map<ResourceType, Integer> mapWithBlanks,
                                                            Map<ResourceType, Integer> blanksRep) throws Exception {
        /* Check that blanksRep does not contain blanks */
        if (blanksRep.keySet().stream().anyMatch(ResourceType::isBlank))
            throw new Exception();

        /* Check that mapWithBlanks has the same number of resources as blanks in blanksRep */
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

        /* Add all replacements resources from blanksRep */
        blanksRep.forEach((r, q) -> replacedBlanks.merge(r, q, Integer::sum));

        return replacedBlanks;
    }

    /**
     * Checks that strongboxes are given for respectively all non-storable resources in a given map.
     *
     * @param mapWithoutBlanks  full map with blanks already replaced
     * @param strongboxes       the map of the strongboxes to use for all the resources
     * @return                  true if valid, otherwise false
     */
    private static boolean checkStrongboxes(Map<ResourceType, Integer> mapWithoutBlanks,
                                            Map<Strongbox, Map<ResourceType, Integer>> strongboxes) {
        /* Filter the storable resources */
        mapWithoutBlanks = mapWithoutBlanks.entrySet().stream()
                .filter(e -> e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));;

        /* Check that mapWithoutBlanks and strongboxes contain the same number of storable resources */
        int mapWithoutBlanksResourcesCount = mapWithoutBlanks.values().stream().reduce(0, Integer::sum);
        int strongboxesResourcesCount = strongboxes.values().stream()
                .map(m -> m.values().stream().reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        if (strongboxesResourcesCount != mapWithoutBlanksResourcesCount)
            return false;

        /* Check that the amount of each storable resource type in mapWithoutBlanks is the same as in strongboxes */
        for (ResourceType resType : mapWithoutBlanks.keySet()) {
            int strongboxesResourceCount = 0;
            for (Strongbox strongbox : strongboxes.keySet())
                strongboxesResourceCount += strongboxes.get(strongbox).getOrDefault(resType, 0);
            if (strongboxesResourceCount != mapWithoutBlanks.get(resType))
                return false;
        }

        return true;
    }
}
