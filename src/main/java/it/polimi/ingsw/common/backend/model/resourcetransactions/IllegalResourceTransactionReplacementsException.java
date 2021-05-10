package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.Map;

/**
 * Exception used in validating transaction requests' replacements.
 */
public class IllegalResourceTransactionReplacementsException extends IllegalResourceTransactionException {
    /**
     * Class constructor.
     *
     * @param reason       the reason why the replacements are incorrect.
     * @param replacements the invalid map of replacements choosen in the resource transaction request.
     */
    public IllegalResourceTransactionReplacementsException(String reason, Map<ResourceType, Integer> replacements) {
        super(String.format("%s. Replacements:\n%s", reason, stringifyMap(replacements)));
    }
}
