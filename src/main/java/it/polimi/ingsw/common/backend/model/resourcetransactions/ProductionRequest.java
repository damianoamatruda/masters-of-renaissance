package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.Map;

/**
 * This class represents the request of a production, i.e. a resource transaction request allowing the use of only
 * strongboxes as output containers.
 */
public class ProductionRequest extends ResourceTransactionRequest {
    public ProductionRequest(ResourceTransactionRecipe recipe,
                             Map<ResourceType, Integer> inputBlanksRep,
                             Map<ResourceType, Integer> outputBlanksRep,
                             Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                             Strongbox outputStrongbox) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        super(recipe, inputBlanksRep, outputBlanksRep, inputContainers, Map.of(outputStrongbox, getReplacedOutput(recipe.getOutput(), outputBlanksRep)));
    }
}
