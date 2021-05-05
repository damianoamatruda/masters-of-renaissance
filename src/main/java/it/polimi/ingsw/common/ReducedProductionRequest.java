package it.polimi.ingsw.common;

import java.util.Map;

public class ReducedProductionRequest {
    private final int productionId;
    private final Map<String, Integer> inputBlanksRep;
    private final Map<String, Integer> outputBlanksRep;
    private final Map<Integer, Map<String, Integer>> inputContainers;
    private final Map<Integer, Map<String, Integer>> outputContainers;


    public ReducedProductionRequest(int productionId, Map<String, Integer> inputBlanksRep, Map<String, Integer> outputBlanksRep,
                                    Map<Integer, Map<String, Integer>> inputContainers,
                                    Map<Integer, Map<String, Integer>> outputContainers) {
        this.productionId = productionId;
        this.inputBlanksRep = inputBlanksRep;
        this.outputBlanksRep = outputBlanksRep;
        this.inputContainers = inputContainers;
        this.outputContainers = outputContainers;
    }

    public int getProductionId() {
        return productionId;
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

    public Map<Integer, Map<String, Integer>> getOutputContainers() {
        return outputContainers;
    }
}
