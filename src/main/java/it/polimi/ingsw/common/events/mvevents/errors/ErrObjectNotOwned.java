package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrObjectNotOwned extends ViewEvent {
    private final int id;
    private final String objectType;

    /**
     * @param view
     * @param id         ID of the object
     * @param objectType type of object the error refers to
     */
    public ErrObjectNotOwned(View view, int id, String objectType) {
        super(view);
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
