package it.polimi.ingsw.common.events.mvevents.Errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrResourceTransfer implements MVEvent {
    private final String resType;
    private final boolean isBoundedResTypeDifferent,
                          isNonStorable,
                          isCapacityReached,
                          isAdded,
                          isDuplicateBoundedResource;

    /**
     * @param resType
     * @param isBoundedResTypeDifferent
     * @param isNonStorable
     * @param isCapacityReached
     * @param isAdded
     * @param isDuplicateBoundedResource
     */
    public ErrResourceTransfer(String resType, boolean isBoundedResTypeDifferent, boolean isNonStorable, boolean isCapacityReached,
            boolean isAdded, boolean isDuplicateBoundedResource) {
        this.resType = resType;
        this.isBoundedResTypeDifferent = isBoundedResTypeDifferent;
        this.isNonStorable = isNonStorable;
        this.isCapacityReached = isCapacityReached;
        this.isAdded = isAdded;
        this.isDuplicateBoundedResource = isDuplicateBoundedResource;
    }

    /**
     * @return the resType
     */
    public String getResType() {
        return resType;
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
