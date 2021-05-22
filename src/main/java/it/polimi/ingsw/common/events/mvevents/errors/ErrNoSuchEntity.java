package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrNoSuchEntity extends ViewEvent {
    private final IDType originalEntity;
    private final int id;
    private final String code;

    public enum IDType {
        MARKET_INDEX,
        LEADER,
        DEVCARD,
        COLOR,
        RESOURCE
    }

    /**
     * @param view
     * @param originalEntity
     * @param id
     * @param code
     */
    public ErrNoSuchEntity(View view, IDType originalEntity, int id, String code) {
        super(view);
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
