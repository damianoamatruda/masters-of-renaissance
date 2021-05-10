package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.Map;
import java.util.stream.Collectors;

public class IllegalResourceTransactionException extends RuntimeException {
    // TODO: Delete
    public IllegalResourceTransactionException(Throwable cause) {
        super(cause);
    }

    public IllegalResourceTransactionException(String msg) {
        super(msg);
    }

    public IllegalResourceTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO more verbose?
    protected static String stringify(ResourceTransactionRequest req) {
        return String.format(
                "Input: %s\nOutput: %s\nInputBlanks: %d\nOutputBlanks: %d\n",
                stringifyMap(req.getRecipe().getInput()),
                stringifyMap(req.getRecipe().getOutput()),
                req.getRecipe().getInputBlanks(),
                req.getRecipe().getOutputBlanks());
    }

    protected static String stringifyMap(Map<ResourceType, Integer> map) {
        return map.keySet().stream()
                .map(key -> key.getName() + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
