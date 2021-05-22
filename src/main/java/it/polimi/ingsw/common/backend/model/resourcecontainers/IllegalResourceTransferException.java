package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

/**
 * Exception thrown when a resource cannot be added or removed.
 */
public class IllegalResourceTransferException extends Exception {
    private final ResourceType resource;
    private final boolean isAdded;

    private final Kind kind;
    
    public enum Kind {
        BOUNDED_RESTYPE_DIFFER,
        NON_STORABLE,
        CAPACITY_REACHED,
        DUPLICATE_BOUNDED_RESOURCE
    }

    /**
     * Class constructor.
     * 
     * @param resource
     * @param isAdded
     * @param kind
     */
    public IllegalResourceTransferException(ResourceType resource,
            boolean isAdded,
            Kind kind) {
                this.kind = kind;
        this.resource = resource;
        this.isAdded = isAdded;
    }

    /**
     * @return the kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * @return the resource
     */
    public ResourceType getResource() {
        return resource;
    }

    /**
     * @return the isAdded
     */
    public boolean isAdded() {
        return isAdded;
    }
}
