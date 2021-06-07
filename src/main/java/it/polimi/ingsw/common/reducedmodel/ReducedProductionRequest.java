package it.polimi.ingsw.common.reducedmodel;

import java.util.HashMap;
import java.util.Map;

public class ReducedProductionRequest {
    private final int production;
    private final Map<String, Integer> inputBlanksRep;
    private final Map<String, Integer> outputBlanksRep;
    private final Map<Integer, Map<String, Integer>> inputContainers;

    public ReducedProductionRequest(int production, Map<String, Integer> inputBlanksRep, Map<String, Integer> outputBlanksRep,
                                    Map<Integer, Map<String, Integer>> inputContainers) {

        if (inputBlanksRep == null)
            inputBlanksRep = new HashMap<>();
        if (outputBlanksRep == null)
            outputBlanksRep = new HashMap<>();
        if (inputContainers == null)
            inputContainers = new HashMap<>();
        
        this.production = production;
        this.inputBlanksRep = inputBlanksRep;
        this.outputBlanksRep = outputBlanksRep;
        this.inputContainers = inputContainers;
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
}
