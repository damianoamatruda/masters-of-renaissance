package it.polimi.ingsw;

import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileResourceTypeFactory implements ResourceTypeFactory {
    Map<String, ResourceType> resourceTypesMap;

    public FileResourceTypeFactory(FileGameFactory factory) {
        resourceTypesMap = factory.generateResourceTypes().stream()
                .collect(Collectors.toMap(ResourceType::getName, Function.identity()));
    }

    public ResourceType get(String name) {
        return resourceTypesMap.get(name);
    }

}
