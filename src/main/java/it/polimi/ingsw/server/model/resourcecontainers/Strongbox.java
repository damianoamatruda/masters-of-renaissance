package it.polimi.ingsw.server.model.resourcecontainers;

import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents an infinite container of resources.
 */
public class Strongbox extends ResourceContainer {
    /** The map of the contained resources. */
    private final Map<ResourceType, Integer> resources;

    /**
     * Initializes the strongbox.
     */
    public Strongbox() {
        resources = new HashMap<>();
    }

    /**
     * Copy constructor. Makes a deep copy of a strongbox.
     *
     * @param strongbox the strongbox to copy
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
        return Set.copyOf(resources.keySet());
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
    public void addResource(ResourceType resType) {
        if (!resType.isStorable())
            throw new IllegalArgumentException(new IllegalResourceTransferException(resType, true));
        resources.compute(resType, (r, q) -> (q == null) ? 1 : q + 1);
    }

    @Override
    public void removeResource(ResourceType resType) {
        if (!resources.containsKey(resType))
            throw new IllegalArgumentException(new IllegalResourceTransferException(resType, false, this));
        if (!resType.isStorable())
            throw new IllegalArgumentException(new IllegalResourceTransferException(resType, false));
        resources.computeIfPresent(resType, (r, q) -> (q == 1) ? null : q - 1);
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }
}
