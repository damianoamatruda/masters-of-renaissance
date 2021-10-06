package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/**
 * Event signaling an error related to a resource transfer between containers.
 */
public class ErrResourceTransfer extends ViewEvent {
    private final String resType;
    private final boolean isAdded;
    private final ErrResourceTransferReason reason;

    /**
     * Class constructor.
     *
     * @param resType the resource type the error relates to
     * @param isAdded whether the resource was trying to be added to a container
     * @param reason  the reson for which the transfer failed
     */
    public ErrResourceTransfer(View view, String resType, boolean isAdded, String reason) {
        super(view);
        this.reason = ErrResourceTransferReason.valueOf(reason);
        this.resType = resType;
        this.isAdded = isAdded;
    }

    /**
     * @return the reason for the transfer failure
     */
    public ErrResourceTransferReason getReason() {
        return reason;
    }

    /**
     * @return the resource type the error originates from
     */
    public String getResType() {
        return resType;
    }

    /**
     * @return whether the resource was trying to be added
     */
    public boolean isAdded() {
        return isAdded;
    }

    public enum ErrResourceTransferReason {
        BOUNDED_RESTYPE_DIFFER,
        NON_STORABLE,
        CAPACITY_REACHED,
        DUPLICATE_BOUNDED_RESOURCE
    }
}
