package it.polimi.ingsw.common.reducedmodel;

import java.util.Map;

public class ReducedProductionRequest {
    private final int production;
    private final Map<String, Integer> inputBlanksRep;
    private final Map<String, Integer> outputBlanksRep;
    private final Map<Integer, Map<String, Integer>> inputContainers;
    private final Map<Integer, Map<String, Integer>> outputStrongboxes;


    public ReducedProductionRequest(int production, Map<String, Integer> inputBlanksRep, Map<String, Integer> outputBlanksRep,
                                    Map<Integer, Map<String, Integer>> inputContainers,
                                    Map<Integer, Map<String, Integer>> outputStrongboxes) {
        this.production = production;
        this.inputBlanksRep = inputBlanksRep;
        this.outputBlanksRep = outputBlanksRep;
        this.inputContainers = inputContainers;
        this.outputStrongboxes = outputStrongboxes;
    }

    public int getProduction() {
        return production;
    }

    public Map<String, Integer> getInputBlanksRep() {
        return inputBlanksRep;
    }

    public Map<String, Integer> getOutputBlanksRep() {
        return outputBlanksRep;
    }

    public Map<Integer, Map<String, Integer>> getInputContainers() {
        return inputContainers;
    }

    public Map<Integer, Map<String, Integer>> getOutputStrongboxes() {
        return outputStrongboxes;
    }
}
