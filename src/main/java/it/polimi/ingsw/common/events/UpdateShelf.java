package it.polimi.ingsw.common.events;

import java.util.Map;

import it.polimi.ingsw.common.View;

/** Resource container state update. */
public class UpdateShelf implements MVEvent { // TODO rename to UpdateResourceContainer
    // TODO reduced and commsproto docs
    /** The ID of the container. */
    private final int id;
    /** The new state of the container. */
    private final Map<String, Integer> content;

    /**
     * Class constructor.
     * 
     * @param id      the ID of the container
     * @param content the new state of the container
     */
    public UpdateShelf(int id, Map<String, Integer> content) {
        this.content = content;
        this.id = id;
    }

    /**
     * @return the new state of the container
     */
    public Map<String, Integer> getContent() {
        return content;
    }

    /**
     * @return the ID of the container
     */
    public int getId() {
        return id;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
