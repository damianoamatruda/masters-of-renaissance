package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrResourceTransfer implements MVEvent {
    private final String resType;
    private final boolean isAdded;
    private final ErrResourceTransferReason reason;

    public enum ErrResourceTransferReason {
        BOUNDEDRESTYPEDIFFER,
        NONSTORABLE,
        CAPACITYREACHED,
        DUPLICATEBOUNDEDRESOURCE
    }
    /**
     * Class constructor.
     * 
     * @param resType
     * @param isAdded
     * @param reason
     */
    public ErrResourceTransfer(String resType,
            boolean isAdded,
            int reason) {
        this.reason = ErrResourceTransferReason.values()[reason];
        this.resType = resType;
        this.isAdded = isAdded;
    }

    /**
     * @return the reason
     */
    public ErrResourceTransferReason getReason() {
        return reason;
    }

    /**
     * @return the resType
     */
    public String getResType() {
        return resType;
    }

    /**
     * @return the isAdded
     */
    public boolean isAdded() {
        return isAdded;
    }
}
