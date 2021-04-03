package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.ResourceType;
import it.polimi.ingsw.strongboxes.Strongbox;

import java.util.HashMap;
import java.util.Map;
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
     * Returns the map of the resources to be given as input of the production.
     *
     * @return  the map of the resources to be given as input
     */
    public Map<ResourceType, Integer> getInput() {
        return input;
    }

    /**
     * Returns the map of the resources to be taken as output of the production.
     *
     * @return  the map of the resources to be taken as output
     */
    public Map<ResourceType, Integer> getOutput() {
        return output;
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
     * @param game              the game the player is playing in
     * @param player            the player on which to trigger the action of the resource, if applicable
     * @param inputBlanksRep    the map of the resources to be given as replacement for blanks in input
     * @param outputBlanksRep   the map of the resources to be taken as replacement for blanks in output
     * @param inputStrongboxes  the map of the strongboxes to use for all the resources to be given as input
     * @param outputStrongboxes the map of the strongboxes to use for all the resources to be taken as output
     * @throws Exception        if it is not possible
     */
    public void activate(Game game, Player player,
                         Map<ResourceType, Integer> inputBlanksRep, Map<ResourceType, Integer> outputBlanksRep,
                         Map<Strongbox, Map<ResourceType, Integer>> inputStrongboxes,
                         Map<Strongbox, Map<ResourceType, Integer>> outputStrongboxes) throws Exception {
        Map<ResourceType, Integer> replacedInput = replaceBlanks(input, inputBlanksRep);
        if (!checkStrongboxes(replacedInput, inputStrongboxes))
            throw new Exception();

        Map<ResourceType, Integer> replacedOutput = replaceBlanks(output, outputBlanksRep);
        if (!checkStrongboxes(replacedOutput, outputStrongboxes))
            throw new Exception();

        transferStorable(true, game, player, inputStrongboxes);
        transferStorable(false, game, player, outputStrongboxes);

        transferNonStorable(true, game, player, filterNonStorable(replacedInput));
        transferNonStorable(false, game, player, filterNonStorable(replacedOutput));
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
        if (!checkBlanksRepBlank(blanksRep))
            throw new Exception();
        if (!blanksRep.isEmpty() && !checkBlanksRepCount(mapWithBlanks, blanksRep))
            throw new Exception();
        Map<ResourceType, Integer> mapWithoutBlanks = mapWithBlanks.entrySet().stream()
                .filter(e -> !e.getKey().isBlank())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        blanksRep.forEach((r, q) -> mapWithoutBlanks.merge(r, q, Integer::sum));
        return mapWithoutBlanks;
    }

    /**
     * Filters the non-storable resources in a given map.
     *
     * @param map   full map of resources
     * @return      map with only non storable resources
     */
    private static Map<ResourceType, Integer> filterNonStorable(Map<ResourceType, Integer> map) {
        return map.entrySet().stream()
                .filter(e -> !e.getKey().isStorable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Transfers storable resources to or from strongboxes in a transaction.
     *
     * @param take          true if transferred from a strongbox, otherwise false
     * @param player        the player on which to trigger the action of the resource, if applicable
     * @param strongboxes   the map of the strongboxes to use for all the resources
     * @throws Exception    if it is not possible
     */
    private void transferStorable(boolean take, Game game, Player player,
                                  Map<Strongbox, Map<ResourceType, Integer>> strongboxes) throws Exception {
        Map<Strongbox, Map<ResourceType, Integer>> history = new HashMap<>();
        for (Map.Entry<Strongbox, Map<ResourceType, Integer>> sEntry : strongboxes.entrySet()) {
            Strongbox strongbox = sEntry.getKey();
            Map<ResourceType, Integer> map = sEntry.getValue();
            for (Map.Entry<ResourceType, Integer> rEntry : map.entrySet()) {
                ResourceType resType = rEntry.getKey();
                int quantity = rEntry.getValue();
                for (int i = 0; i < quantity; i++) {
                    try {
                        if (take)
                            resType.onTaken(game, player, strongbox);
                        else
                            resType.onGiven(game, player, strongbox);
                    } catch (Exception e) {
                        if (discardableOutput)
                            resType.onDiscard(game, player, strongbox);
                        else {
                            transferStorable(!take, game, player, history);
                            throw new Exception();
                        }
                    }
                    history.computeIfAbsent(strongbox, s -> new HashMap<>())
                            .compute(resType, (r, q) -> (q == null) ? 1 : q + 1);
                }
            }
        }
    }

    /**
     * Transfers non-storable resources to or from a player.
     *
     * @param take          true if transferred from a strongbox, otherwise false
     * @param player        the player on which to trigger the action of the resource, if applicable
     * @param resources     the map of the resources
     * @throws Exception    if it is not possible
     */
    private void transferNonStorable(boolean take, Game game, Player player,
                                     Map<ResourceType, Integer> resources) throws Exception {
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            ResourceType r = entry.getKey();
            int q = entry.getValue();
            for (int i = 0; i < q; i++)
                if (take)
                    r.onTaken(game, player, new Strongbox());
                else
                    r.onGiven(game, player, new Strongbox());
        }
    }

    /**
     * Checks that <code>blanksRep</code> does not contain blanks
     *
     * @param blanksRep     map of replacement of blanks
     * @return              true if valid, otherwise false
     */
    private static boolean checkBlanksRepBlank(Map<ResourceType, Integer> blanksRep) {
        return blanksRep.keySet().stream().noneMatch(ResourceType::isBlank);
    }

    /**
     * Checks that <code>blanksRep</code> has the same number of resources as blanks in <code>mapWithBlanks</code>
     *
     * @param mapWithBlanks full map including blanks
     * @param blanksRep     map of replacement of blanks
     * @return              true if valid, otherwise false
     */
    private static boolean checkBlanksRepCount(Map<ResourceType, Integer> mapWithBlanks,
                                               Map<ResourceType, Integer> blanksRep) {
        int blanksCount = mapWithBlanks.entrySet().stream()
                .filter(e -> e.getKey().isBlank()).map(Map.Entry::getValue).reduce(0, Integer::sum);
        int blanksRepCount = blanksRep.values().stream().reduce(0, Integer::sum);
        return blanksRepCount == blanksCount;
    }

    /**
     * Checks that <code>strongboxes</code> are given for respectively all resources in <code>mapWithoutBlanks</code>
     *
     * Note: Strongboxes cannot contain non-storable resources
     *
     * @param mapWithoutBlanks  full map with blanks already replaces
     * @param strongboxes       the map of the strongboxes to use for all the resources
     * @return                  true if valid, otherwise false
     */
    private static boolean checkStrongboxes(Map<ResourceType, Integer> mapWithoutBlanks,
                                            Map<Strongbox, Map<ResourceType, Integer>> strongboxes) {
        for (Map.Entry<ResourceType, Integer> inputEntry : mapWithoutBlanks.entrySet()) {
            ResourceType inputResource = inputEntry.getKey();
            int inputQuantity = inputResource.isStorable() ? inputEntry.getValue() : 0;

            int strongboxesQuantity = 0;
            for (Map<ResourceType, Integer> inputStrongboxValue: strongboxes.values()) {
                for (Map.Entry<ResourceType, Integer> inputStrongboxEntry : inputStrongboxValue.entrySet()) {
                    if (inputStrongboxEntry.getKey() == inputResource)
                        strongboxesQuantity += inputStrongboxEntry.getValue();
                }
            }

            if (strongboxesQuantity != inputQuantity)
                return false;
        }
        return true;
    }
}
