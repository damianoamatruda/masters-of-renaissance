package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrResourceTransfer implements MVEvent {
    private final String resType;
    private final boolean isAdded;
    private final int kind;

    /**
     * Class constructor.
     * 
     * @param resType
     * @param isAdded
     * @param kind
     */
    public ErrResourceTransfer(String resType,
            boolean isAdded,
            int kind) {
        this.kind = kind;
        this.resType = resType;
        this.isAdded = isAdded;
    }

    /**
     * @return the kind
     */
    public int getKind() {
        return kind;
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
