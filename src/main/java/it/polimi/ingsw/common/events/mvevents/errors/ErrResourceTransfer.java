package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrResourceTransfer extends ViewEvent {
    private final String resType;
    private final boolean isAdded;
    private final ErrResourceTransferReason reason;

    public enum ErrResourceTransferReason {
        BOUNDED_RESTYPE_DIFFER,
        NON_STORABLE,
        CAPACITY_REACHED,
        DUPLICATE_BOUNDED_RESOURCE
    }

    /**
     * Class constructor.
     *
     * @param resType
     * @param isAdded
     * @param reason
     */
    public ErrResourceTransfer(View view, String resType, boolean isAdded, String reason) {
        super(view);
        this.reason = ErrResourceTransferReason.valueOf(reason);
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
