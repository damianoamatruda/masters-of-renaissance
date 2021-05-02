package it.polimi.ingsw.server.model;

import java.util.Map;
import java.util.stream.Collectors;

import it.polimi.ingsw.server.model.ProductionGroup.ProductionRequest;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

/**
 * Exception thrown when a production cannot be activated with the given options.
 */
public class IllegalProductionActivationException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     * @see Exception
     */
    public IllegalProductionActivationException(String message, ProductionRequest req) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     * @see Exception
     */
    public IllegalProductionActivationException(String message, ProductionRequest req, Throwable cause) {
        super(String.format("%s\n%s", message, stringify(req)), cause);
    }

    // TODO more verbose?
    private static String stringify(ProductionRequest req) {
        return String.format(
            "Input: %s\nOutput: %s\nInputBlanks: %d\nOutputBlanks: %d\n",
                stringifyMap(req.getProduction().getInput()),
                stringifyMap(req.getProduction().getOutput()),
                req.getProduction().getInputBlanks(),
                req.getProduction().getOutputBlanks());
    }

    private static String stringifyMap(Map<ResourceType, Integer> map) {
        return map.keySet().stream()
            .map(key -> key.getName() + "=" + String.valueOf(map.get(key)))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * Exception used in validating production requests' replacements.
     */
    public static class IllegalProductionReplacementsException extends Exception {
        /**
         * Class constructor.
         * 
         * @param reason the reason why the replacements are incorrect.
         * @param replacements the invalid map of replacements choosen in the production request.
         */
        public IllegalProductionReplacementsException(String reason, Map<ResourceType, Integer> replacements) {
            super(String.format("%s. Replacements:\n%s", reason, stringifyMap(replacements)));
        }
    }

    /**
     * Exception used in validating production requests' containers.
     */
    public static class IllegalProductionContainersException extends Exception { // not used for checks, instances could be replaced by superclass
        /**
         * Class constructor.
         * 
         * @param msg the reason for this exception.
         */
        public IllegalProductionContainersException(String msg) {
            super(msg);
        }
    }
}
