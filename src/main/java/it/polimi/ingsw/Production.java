package it.polimi.ingsw;

import java.util.Map;

/**
 * A class representing a production of resources.
 */
public class Production {
    /** The map of the resources, including blanks, to be given as input. */
    private final Map<ResourceType, Integer> input;

    /** The map of the resources, including blanks, to be taken as output. */
    private final Map<ResourceType, Integer> output;

    /**
     * Initializes the production specifying its input and its output.
     *
     * @param input     the map of the resources to be given as input of the production
     * @param output    the map of the resources to be taken as output of the production
     * */
    public Production(Map<ResourceType, Integer> input, Map<ResourceType, Integer> output) {
        this.input = input;
        this.output = output;
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
     * Activates the production. Replaces the blanks in input and in output, if applicable, with the given resources,
     * removes the input resources from the given strongboxes and adds the output resources in the given strongbox.
     *
     * @param inputBlanks       the map of the resources to be given as replacement for blanks in input
     * @param outputBlanks      the map of the resources to be taken as replacement for blanks in output
     * @param inputStrongboxes  the map of the strongboxes to use for all the resources to be given as input
     * @param outputStrongbox   the strongbox to use for all the resources to be taken as output
     */
    public void activate(Map<ResourceType, Integer> inputBlanks, Map<ResourceType, Integer> outputBlanks,
                         Map<ResourceType, Map<Strongbox, Integer>> inputStrongboxes, Strongbox outputStrongbox) {

    }

    /**
     * Returns if the production is empty.
     *
     * @return  true if the production has empty input and empty output, otherwise false
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Checks if the production can be activated with the given parameters.
     *
     * @param inputBlanks       the map of the resources to be given as replacement of blanks in input
     * @param outputBlanks      the map of the resources to be taken as replacement of blanks in output
     * @param inputStrongboxes  the map of the strongboxes to use for each resource to be given as input
     * @param outputStrongbox   the strongbox to use for all the resources to be taken as output
     * @return                  the outcome
     */
    private boolean canActivate(Map<ResourceType, Integer> inputBlanks, Map<ResourceType, Integer> outputBlanks,
                                Map<ResourceType, Map<Strongbox, Integer>> inputStrongboxes, Strongbox outputStrongbox) {
        return false;
    }
}
