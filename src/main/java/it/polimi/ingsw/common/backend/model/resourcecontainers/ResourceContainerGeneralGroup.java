package it.polimi.ingsw.common.backend.model.resourcecontainers;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a group of general replaceable resource containers.
 *
 * @see ResourceContainerGroup
 */
public class ResourceContainerGeneralGroup implements ResourceContainerGroup {
    private final List<ResourceContainer> resContainers;

    public ResourceContainerGeneralGroup(List<ResourceContainer> resContainers) {
        this.resContainers = new ArrayList<>(resContainers);
    }

    public ResourceContainerGeneralGroup(ResourceContainerGroup resourceContainerGroup) {
        resContainers = new ArrayList<>(resourceContainerGroup.getResourceContainers());
    }

    @Override
    public List<ResourceContainer> getResourceContainers() {
        return List.copyOf(resContainers);
    }

    public void replaceResourceContainer(ResourceContainer oldContainer, ResourceContainer newContainer) {
        resContainers.replaceAll(c -> c.equals(oldContainer) ? newContainer : c);
    }
}
