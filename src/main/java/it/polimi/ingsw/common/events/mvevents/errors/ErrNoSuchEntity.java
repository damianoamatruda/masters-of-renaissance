package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrNoSuchEntity implements MVEvent {
    private final IDType originalEntity;
    private final int id;
    private final String code;
    
    public enum IDType {
        MARKETINDEX,
        LEADER,
        DEVCARD,
        COLOR,
        RESOURCE
    }
    
    /**
     * @param originalEntity
     * @param id
     * @param code
     */
    public ErrNoSuchEntity(IDType originalEntity, int id, String code) {
        this.originalEntity = originalEntity;
        this.id = id;
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the originalEntity
     */
    public IDType getOriginalEntity() {
        return originalEntity;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

}
