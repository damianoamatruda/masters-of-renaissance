package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a container of resources in unlimited quantity.
 */
public class Strongbox implements ResourceContainer {
    /** The map of the contained resources. */
    private final Map<ResourceType, Integer> resources;

    /**
     * Initializes the strongbox.
     */
    public Strongbox() {
        resources = new HashMap<>();
    }

    /**
     * Copy constructor. Makes a deep copy of a Strongbox.
     *
     * @param strongbox the Strongbox to copy
     */
    public Strongbox(Strongbox strongbox) {
        resources = new HashMap<>(strongbox.resources);
    }

    @Override
    public ResourceContainer copy() {
        return new Strongbox(this);
    }

    @Override
    public Set<ResourceType> getResourceTypes() {
        return resources.keySet();
    }

    @Override
    public int getQuantity() {
        return resources.values().stream().reduce(0, Integer::sum);
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resources.getOrDefault(resType, 0);
    }

    @Override
    public void addResource(ResourceType resType) throws Exception {
        if (!resType.isStorable())
            throw new Exception();
        resources.compute(resType, (r, q) -> (q == null) ? 1 : q + 1);
    }

    @Override
    public void removeResource(ResourceType resType) throws Exception {
        if (!resources.containsKey(resType))
            throw new Exception();
        resources.computeIfPresent(resType, (r, q) -> (q == 1) ? null : q - 1);
    }

    @Override
    public void addAll(ResourceContainer resourceContainer) throws Exception {
        for (ResourceType resType : resourceContainer.getResourceTypes())
            for (int i = 0; i < resourceContainer.getResourceQuantity(resType); i++)
                addResource(resType);
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }
}
