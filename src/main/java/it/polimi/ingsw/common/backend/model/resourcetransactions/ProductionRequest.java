package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer.mergeResourceMaps;

/**
 * This class represents the request of a production, i.e. a resource transaction request allowing the use of only
 * strongboxes as output containers.
 */
public class ProductionRequest extends ResourceTransactionRequest {
    public ProductionRequest(ResourceTransactionRecipe recipe,
                             Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                             Map<ResourceType, Integer> inputNonStorableRep,
                             Strongbox outputStrongbox,
                             Map<ResourceType, Integer> outputRep) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        super(
                recipe,
                inputContainers,
                inputNonStorableRep,
                Map.of(outputStrongbox, mergeResourceMaps(
                        recipe.getOutput().entrySet().stream()
                                .filter(e -> e.getKey().isStorable())
                                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)),
                        outputRep.entrySet().stream()
                                .filter(e -> e.getKey().isStorable())
                                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)))),
                outputRep.entrySet().stream()
                        .filter(e -> !e.getKey().isStorable())
                        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
