package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void addResources(Map<ResourceType, Integer> resMap) {
        if (!resMap.keySet().stream().allMatch(ResourceType::isStorable)) {
            ResourceType nonStorable = resMap.keySet().stream().filter(r -> !r.isStorable()).findAny().orElseThrow();
            throw new IllegalArgumentException(
                new IllegalResourceTransferException(nonStorable, false, true, false, true, false));
        }
        for (ResourceType resType : resMap.keySet())
            resources.compute(resType, (r, q) -> q == null ? resMap.get(resType) : q + resMap.get(resType));

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void addResource(ResourceType resType) {
        addResources(Map.of(resType, 1));
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resMap) {
        if (!resMap.keySet().stream().allMatch(resources::containsKey)) {
            ResourceType resType = resMap.keySet().stream().filter(r -> !resources.containsKey(r)).findAny().orElseThrow();
            throw new IllegalArgumentException(
                new IllegalResourceTransferException(resType, false, false, false, false, false));
        }
        if (!resMap.keySet().stream().allMatch(ResourceType::isStorable)) {
            ResourceType nonStorable = resMap.keySet().stream().filter(r -> !r.isStorable()).findAny().orElseThrow();
            throw new IllegalArgumentException(
                new IllegalResourceTransferException(nonStorable, false, true, false, false, false));
        }
        for (ResourceType resType : resMap.keySet())
            if (resources.get(resType) < resMap.get(resType))
                throw new IllegalArgumentException(
                    new IllegalResourceTransferException(resType, false, false, true, false, false));
        for (ResourceType resType : resMap.keySet())
            resources.computeIfPresent(resType, (r, q) -> q.equals(resMap.get(resType)) ? null : q - resMap.get(resType));

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void removeResource(ResourceType resType) {
        removeResources(Map.of(resType, 1));
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), resources.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)), null);
    }
}
