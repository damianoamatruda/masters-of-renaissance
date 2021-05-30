package it.polimi.ingsw.common.backend.model.resourcecontainers;

import java.util.List;

/**
 * This interface represents a group of resource containers.
 */
public interface ResourceContainerGroup {
    /**
     * Returns the resource containers inside the resource container group
     *
     * @return the list of the resource containers
     */
    List<ResourceContainer> getResourceContainers();
}
