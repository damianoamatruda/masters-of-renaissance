package it.polimi.ingsw;

import it.polimi.ingsw.model.resourcetypes.ResourceType;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileResourceTypeFactory implements ResourceTypeFactory {
    Map<String, ResourceType> resourceTypesMap;
    ModelConfig config;

    public FileResourceTypeFactory(ModelConfig config) {
        this.config = config;
        resourceTypesMap = generateResourceTypes().stream()
                .collect(Collectors.toMap(ResourceType::getName, Function.identity()));
    }

    public ResourceType get(String name) {
        return resourceTypesMap.get(name);
    }

    public Set<ResourceType> generateResourceTypes(){
        return config.getResourceTypes().stream().map(s -> new ResourceType(s.getName(), s.isStorable())).collect(Collectors.toSet());
    }

}
