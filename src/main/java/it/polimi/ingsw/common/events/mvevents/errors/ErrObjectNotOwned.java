package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrObjectNotOwned implements MVEvent {
    private final int id;

    /**
     * @param id
     */
    public ErrObjectNotOwned(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
}
