package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/**
 * Event signaling an error when trying to identify a non-existent entity.
 */
public class ErrNoSuchEntity extends ViewEvent {
    private final IDType originalEntity;
    private final int id;
    private final String code;

    /**
     * @param view
     * @param originalEntity type of entity trying to be referred
     * @param id             ID of the object
     * @param code           name of the color/resource
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

    public enum IDType {
        MARKET_INDEX,
        LEADER,
        DEVCARD,
        COLOR,
        RESOURCE
    }

}
