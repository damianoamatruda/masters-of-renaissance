package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrObjectNotOwned implements MVEvent {
    private final int id;
    private final String objectType;

    /**
     * @param id
     */
    public ErrObjectNotOwned(int id, String objectType) {
        this.id = id;
        this.objectType = objectType;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }
}
