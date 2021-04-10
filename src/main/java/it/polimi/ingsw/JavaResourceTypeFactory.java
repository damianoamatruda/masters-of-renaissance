package it.polimi.ingsw;

import it.polimi.ingsw.model.resourcetypes.IncrementFaithPointsResType;
import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;

import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaResourceTypeFactory implements ResourceTypeFactory {
    Map<String, ResourceType> resourceTypesMap;

    public JavaResourceTypeFactory() {
        resourceTypesMap = generateResourceTypes().stream()
                .collect(Collectors.toMap(ResourceType::getName, Function.identity()));
    }

    public ResourceType get(String name) {
        return resourceTypesMap.get(name);
    }

    private Set<ResourceType> generateResourceTypes() {
        return Set.of(
                new ResourceType("Coin", true),
                new IncrementFaithPointsResType("Faith", false),
                new ResourceType("Servant", true),
                new ResourceType("Shield", true),
                new ResourceType("Stone", true),
                new ResourceType("Zero", false)
        );
    }
}
