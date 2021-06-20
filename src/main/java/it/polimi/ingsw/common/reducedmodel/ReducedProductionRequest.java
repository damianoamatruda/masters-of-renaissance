package it.polimi.ingsw.common.reducedmodel;

import java.util.HashMap;
import java.util.Map;

public class ReducedProductionRequest {
    private final int production;
    private final Map<Integer, Map<String, Integer>> inputContainers;
    private final Map<String, Integer> inputNonStorableRep;
    private final Map<String, Integer> outputRep;

    public ReducedProductionRequest(int production, Map<Integer, Map<String, Integer>> inputContainers,
                                    Map<String, Integer> inputNonStorableRep, Map<String, Integer> outputRep) {
        if (inputContainers == null)
            inputContainers = new HashMap<>();
        if (inputNonStorableRep == null)
            inputNonStorableRep = new HashMap<>();
        if (outputRep == null)
            outputRep = new HashMap<>();

        this.production = production;
        this.inputContainers = inputContainers;
        this.inputNonStorableRep = inputNonStorableRep;
        this.outputRep = outputRep;
    }

    public int getProduction() {
        return production;
    }

    public Map<Integer, Map<String, Integer>> getInputContainers() {
        return inputContainers;
    }

    public Map<String, Integer> getInputNonStorableRep() {
        return inputNonStorableRep;
    }

    public Map<String, Integer> getOutputRep() {
        return outputRep;
    }
}
