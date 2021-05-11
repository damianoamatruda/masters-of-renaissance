package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;

public class ReducedResourceTransactionRecipe {
    private final int id;
    private final Map<String, Integer> input;
    private final int inputBlanks;
    private final List<String> inputBlanksExclusions;
    private final Map<String, Integer> output;
    private final int outputBlanks;
    private final List<String> outputBlanksExclusions;
    private final boolean discardableOutput;
    
    /**
     * @param id
     * @param input
     * @param inputBlanks
     * @param inputBlanksExclusions
     * @param output
     * @param outputBlanks
     * @param outputBlanksExclusions
     * @param discardableOutput
     */
    public ReducedResourceTransactionRecipe(int id, Map<String, Integer> input, int inputBlanks,
            List<String> inputBlanksExclusions, Map<String, Integer> output, int outputBlanks,
            List<String> outputBlanksExclusions, boolean discardableOutput) {
        this.id = id;
        this.input = input;
        this.inputBlanks = inputBlanks;
        this.inputBlanksExclusions = inputBlanksExclusions;
        this.output = output;
        this.outputBlanks = outputBlanks;
        this.outputBlanksExclusions = outputBlanksExclusions;
        this.discardableOutput = discardableOutput;
    }

    /**
     * @return the id of the recipe
     */
    public int getId() {
        return id;
    }

    /**
     * @return the input resources of the recipe
     */
    public Map<String, Integer> getInput() {
        return input;
    }

    /**
     * @return the inputBlanks of the recipe
     */
    public int getInputBlanks() {
        return inputBlanks;
    }

    /**
     * @return the inputBlanksExclusions of the recipe
     */
    public List<String> getInputBlanksExclusions() {
        return inputBlanksExclusions;
    }

    /**
     * @return the output resources of the recipe
     */
    public Map<String, Integer> getOutput() {
        return output;
    }

    /**
     * @return the outputBlanks of the recipe
     */
    public int getOutputBlanks() {
        return outputBlanks;
    }

    /**
     * @return the outputBlanksExclusions of the recipe
     */
    public List<String> getOutputBlanksExclusions() {
        return outputBlanksExclusions;
    }

    /**
     * @return whether the output of the recipe is discardable
     */
    public boolean isDiscardableOutput() {
        return discardableOutput;
    }
}
