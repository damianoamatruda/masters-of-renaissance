package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.Map;
import java.util.Set;

/**
 * This class represents a production, that is a transaction of transfers of resources from and to resource containers
 * and a player.
 */
public class Production {
    /** The map of the input resources. */
    private final Map<ResourceType, Integer> input;

    /** The number of the input blanks. */
    private final int inputBlanks;

    /** The resources that cannot replace input blanks. */
    private final Set<ResourceType> inputBlanksExclusions;

    /** The map of the output resources. */
    private final Map<ResourceType, Integer> output;

    /** The number of the output blanks. */
    private final int outputBlanks;

    /** The resources that cannot replace output blanks. */
    private final Set<ResourceType> outputBlanksExclusions;

    /** <code>true</code> if the output can be discarded; <code>false</code> otherwise. */
    private final boolean discardableOutput;

    /**
     * Initializes the production specifying its input and its output.
     *
     * @param input                  the map of the resources to be given as input of the production
     * @param inputBlanks            the number of the input blanks
     * @param inputBlanksExclusions  the resources that cannot replace input blanks
     * @param output                 the map of the resources to be taken as output of the production
     * @param outputBlanks           the number of the output blanks
     * @param outputBlanksExclusions the resources that cannot replace output blanks
     * @param discardableOutput      <code>true</code> if the output can be discarded; <code>false</code> otherwise.
     */
    public Production(Map<ResourceType, Integer> input, int inputBlanks, Set<ResourceType> inputBlanksExclusions,
                      Map<ResourceType, Integer> output, int outputBlanks, Set<ResourceType> outputBlanksExclusions,
                      boolean discardableOutput) {
        this.input = Map.copyOf(input);
        this.inputBlanks = inputBlanks;
        this.inputBlanksExclusions = Set.copyOf(inputBlanksExclusions);
        this.output = Map.copyOf(output);
        this.outputBlanks = outputBlanks;
        this.outputBlanksExclusions = Set.copyOf(outputBlanksExclusions);
        this.discardableOutput = discardableOutput;
    }

    /**
     * Initializes a production with no exclusions and non-discardable output specifying its input and its output.
     *
     * @param input        the map of the resources to be given as input of the production
     * @param inputBlanks  the number of the input blanks
     * @param output       the map of the resources to be taken as output of the production
     * @param outputBlanks the number of the input blanks
     */
    public Production(Map<ResourceType, Integer> input, int inputBlanks,
                      Map<ResourceType, Integer> output, int outputBlanks) {
        this(input, inputBlanks, Set.of(), output, outputBlanks, Set.of(), false);
    }

    /**
     * Returns the map of the input resources of the production.
     *
     * @return the map of the input resources
     */
    public Map<ResourceType, Integer> getInput() {
        return input;
    }

    /**
     * Returns the number of the input blanks of the production.
     *
     * @return the number of the input blanks
     */
    public int getInputBlanks() {
        return inputBlanks;
    }

    /**
     * Returns the input blanks exclusions of the production.
     *
     * @return the input blanks exclusions
     */
    public Set<ResourceType> getInputBlanksExclusions() {
        return inputBlanksExclusions;
    }

    /**
     * Returns the map of the output resources of the production.
     *
     * @return the map of the output resources
     */
    public Map<ResourceType, Integer> getOutput() {
        return output;
    }

    /**
     * Returns the number of the output blanks of the production.
     *
     * @return the number of the output blanks
     */
    public int getOutputBlanks() {
        return outputBlanks;
    }

    /**
     * Returns the output blanks exclusions of the production.
     *
     * @return the output blanks exclusions
     */
    public Set<ResourceType> getOutputBlanksExclusions() {
        return outputBlanksExclusions;
    }

    /**
     * Returns whether the production has discardable output.
     *
     * @return <code>true</code> if the output can be discarded; <code>false</code> otherwise.
     */
    public boolean hasDiscardableOutput() {
        return discardableOutput;
    }
}
