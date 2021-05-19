package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrObjectNotOwned implements MVEvent {
    private final int id;
    private final String type;

    /**
     * @param id
     */
    public ErrObjectNotOwned(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}
