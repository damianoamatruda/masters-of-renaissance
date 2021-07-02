package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException.Kind;
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
        super(strongbox.getId(), strongbox.group);
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
    public Map<ResourceType, Integer> getResourceMap() {
        return Map.copyOf(resources);
    }

    @Override
    public int getResourceQuantity(ResourceType resType) {
        return resources.getOrDefault(resType, 0);
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public void addResources(Map<ResourceType, Integer> resMap) {
        validateStorableResourceMap(resMap, true);

        resMap.forEach((r, q) -> resources.merge(r, q, Integer::sum));

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resMap) throws IllegalResourceTransferException {
        validateStorableResourceMap(resMap, true);

        for (ResourceType resType : resMap.keySet())
            if (resources.getOrDefault(resType, 0) < resMap.get(resType))
                throw new IllegalResourceTransferException(resType, false, Kind.CAPACITY_REACHED);

        for (ResourceType resType : resMap.keySet())
            resources.computeIfPresent(resType, (r, q) -> q.equals(resMap.get(resType)) ? null : q - resMap.get(resType));

        dispatch(new UpdateResourceContainer(reduce()));
    }

    @Override
    public ReducedResourceContainer reduce() {
        return new ReducedResourceContainer(getId(), -1, resources.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)), null);
    }
}
