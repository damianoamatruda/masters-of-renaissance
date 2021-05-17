package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

/**
 * Exception thrown when a resource cannot be added or removed.
 */
public class IllegalResourceTransferException extends Exception {
    private final ResourceType resource;
    private final boolean isBoundedResTypeDifferent,
                          isNonStorable,
                          isCapacityReached,
                          isAdded,
                          isDuplicateBoundedResource;

    /**
     * @param resource
     * @param isBoundedResTypeDifferent
     * @param isNonStorable
     * @param isCapacityReached
     * @param isAdded
     * @param isDuplicateBoundedResource
     */
    public IllegalResourceTransferException(ResourceType resource, boolean isBoundedResTypeDifferent, boolean isNonStorable, boolean isCapacityReached,
            boolean isAdded, boolean isDuplicateBoundedResource) {
        this.resource = resource;
        this.isBoundedResTypeDifferent = isBoundedResTypeDifferent;
        this.isNonStorable = isNonStorable;
        this.isCapacityReached = isCapacityReached;
        this.isAdded = isAdded;
        this.isDuplicateBoundedResource = isDuplicateBoundedResource;
    }

    /**
     * @return the resource
     */
    public ResourceType getResource() {
        return resource;
    }

    /**
     * @return the isBoundedResTypeDifferent
     */
    public boolean isBoundedResTypeDifferent() {
        return isBoundedResTypeDifferent;
    }

    /**
     * @return the isDuplicateBoundedResource
     */
    public boolean isDuplicateBoundedResource() {
        return isDuplicateBoundedResource;
    }

    /**
     * @return the isAdded
     */
    public boolean isAdded() {
        return isAdded;
    }

    /**
     * @return the isCapacityReached
     */
    public boolean isCapacityReached() {
        return isCapacityReached;
    }

    /**
     * @return the isNonStorable
     */
    public boolean isNonStorable() {
        return isNonStorable;
    }
}
